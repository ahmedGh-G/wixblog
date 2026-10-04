package com.tech.wixblog.media.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.dto.InlineImageUploadResponse;
import com.tech.wixblog.media.dto.MediaUploadResponse;
import com.tech.wixblog.media.repository.MediaAssetRepository;
import com.tech.wixblog.media.validation.ImageValidator;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * Orchestrates validation, storage and bookkeeping for uploaded images.
 * <p>
 * <b>Transaction boundary.</b> Filesystem writes are not transactional and cannot be
 * rolled back by Spring. Each method therefore writes the bytes first, persists the
 * ledger row second, and compensates by deleting the file if the persist fails. This
 * ordering means a crash can leave an orphaned file (harmless, swept by cleanup) but
 * never a published URL pointing at nothing (broken images in production).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaStorage mediaStorage;
    private final MediaAssetRepository mediaAssetRepository;
    private final UserRepository userRepository;
    private final ImageValidator imageValidator;
    private final MediaProperties mediaProperties;

    /* ------------------------------------------------------------------ */
    /* Uploads                                    */
    /* ------------------------------------------------------------------ */

    /**
     * Validates and stores an image for the given scope, returning the descriptor the
     * client submits back in the JSON body of the owning resource.
     */
    @Transactional
    public MediaUploadResponse store (
            MultipartFile file,
            MediaScope scope,
            UUID ownerId
                                   ) {
        User owner = requireUser(ownerId);
        ImageValidator.ValidatedImage image = imageValidator.validate(file, scope);

        String storageKey = generateStorageKey(scope, image.extension());
        String publicUrl = buildPublicUrl(storageKey);

        writeFile(storageKey, image.content());

        MediaAsset asset = new MediaAsset(
                storageKey,
                publicUrl,
                scope,
                owner,
                sanitiseOriginalFilename(file.getOriginalFilename()),
                image.contentType(),
                image.sizeBytes(),
                image.width(),
                image.height()
                              );
        try {
            /*
             * saveAndFlush, not save. JpaRepository.save() on a new entity only
             * queues the INSERT via persist(); constraints are evaluated at flush or
             * commit, which happens after this method has already returned. Using save()
             * meant a not-null, unique or foreign-key violation never reached the catch
             * block below, so the file was written and then orphaned on disk while the
             * caller received a 409. Flushing here puts the INSERT inside the try, which
             * is what makes the compensation actually reachable.
             */
            return toResponse(mediaAssetRepository.saveAndFlush(asset));
        } catch (RuntimeException exception) {
            // The ledger row never landed, so the bytes are unreachable. Drop them
            // rather than leaving a file nothing will ever point at again.
            compensate(storageKey, exception);
            throw exception;
        }
    }

    /**
     * Stores an image destined for a story body and shapes the result as an
     * Editor.js image block, so the frontend can insert the response directly into
     * the document without reshaping it.
     */
    @Transactional
    public InlineImageUploadResponse storeInlineImage (MultipartFile file, UUID ownerId) {
        MediaUploadResponse upload = store(file, MediaScope.INLINE_IMAGE, ownerId);
        return InlineImageUploadResponse.forEditorJs(upload);
    }

    /* ------------------------------------------------------------------ */
    /* Reference resolution                        */
    /* ------------------------------------------------------------------ */

    /**
     * Resolves a client-supplied image reference supplied on a create or update
     * request and confirms the caller is entitled to attach it.
     * <p>
     * This is the ownership gate. Without it any authenticated user could claim any
     * media URL they had seen, including another user's avatar or an unpublished
     * inline image.
     *
     * @param value    the URL as supplied, may be {@code null} or blank
     * @param scope    the scope the reference must belong to
     * @param ownerId  the authenticated user
     * @return the resolved asset, or empty when no reference was supplied
     * @throws BusinessRuleException when a reference is supplied but is not usable
     */
    @Transactional(readOnly = true)
    public Optional<MediaAsset> resolveOwnedReference (String value, MediaScope scope, UUID ownerId) {
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }
        String candidate = value.trim();

        if (isManagedUrl(candidate)) {
            return Optional.of(
                    mediaAssetRepository.findOwnedActiveByPublicUrl(candidate, scope, ownerId)
                            .orElseThrow(() -> new BusinessRuleException(
                                    "The referenced image is unavailable or does not belong to you."
                            ))
                                      );
        }

        if (!mediaProperties.allowExternalUrls()) {
            throw new BusinessRuleException(
                    "Only images uploaded through this application may be attached."
            );
        }
        return Optional.empty();
    }

    /**
     * Resolves many references at once, used when validating every inline image in a
     * story body. Returns the subset that resolved; the caller compares counts to
     * detect a bad reference without an N+1 query per image.
     */
    @Transactional(readOnly = true)
    public List<MediaAsset> resolveOwnedReferences (
            List<String> values,
            MediaScope scope,
            UUID ownerId
                                                ) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return mediaAssetRepository.findOwnedActiveByPublicUrlIn(
                values.stream().distinct().toList(),
                ownerId
                                                  )
                                  .stream()
                                  .filter(asset -> asset.getScope() == scope)
                                  .toList();
    }

    /**
     * Retires the asset currently referenced by {@code currentReference} and physically
     * removes its file, now that {@code replacement} has taken its place.
     * <p>
     * Failures are logged and swallowed: a cover or avatar update must still succeed
     * even if the old file cannot be removed. The row is marked deleted either way, so
     * the cleanup sweep can retry.
     */
    @Transactional
    public void retireReplacedReference (String currentReference, UUID ownerId) {
        if (!StringUtils.hasText(currentReference)) {
            return;
        }
        String candidate = currentReference.trim();
        if (!isManagedUrl(candidate)) {
            // Someone else's file, or a hotlink. Not ours to delete.
            return;
        }
        mediaAssetRepository.findOwnedActiveByPublicUrl(candidate, MediaScope.AVATAR, ownerId)
                .or(() -> mediaAssetRepository.findOwnedActiveByPublicUrl(candidate, MediaScope.STORY_COVER, ownerId))
                .ifPresent(this::retireAndDelete);
    }

    /* ------------------------------------------------------------------ */
    /* Housekeeping                               */
    /* ------------------------------------------------------------------ */

    /**
     * Retires and removes a single asset. Exposed for the cleanup sweep.
     */
    @Transactional
    public void retireAndDelete (MediaAsset asset) {
        asset.markDeleted();
        mediaAssetRepository.save(asset);
        try {
            mediaStorage.delete(asset.getStorageKey());
        } catch (IOException exception) {
            log.warn(
                    "Failed to delete media file for key {}. It will be retried by the cleanup sweep.",
                    asset.getStorageKey(),
                    exception
            );
        }
    }

    @Transactional(readOnly = true)
    public Optional<MediaAsset> findActiveByPublicUrl (String publicUrl) {
        if (!StringUtils.hasText(publicUrl)) {
            return Optional.empty();
        }
        return mediaAssetRepository.findByPublicUrl(publicUrl.trim())
                                  .filter(asset -> !asset.isDeleted());
    }

    /* ------------------------------------------------------------------ */
    /* Internals                                                */
    /* ------------------------------------------------------------------ */

    private User requireUser (UUID ownerId) {
        return userRepository.findById(ownerId)
                             .orElseThrow(() -> new ResourceNotFoundException(
                                     "User not found."
                             ));
    }

    /**
     * Builds {@code <scope>/<yyyy>/<MM>/<random>.ext}.
     * <p>
     * The date segments exist so that a directory listing of the storage root stays
     * navigable once tens of thousands of files are present. The name is 122 bits of
     * {@link UUID} entropy and nothing else: the client's filename is never used,
     * which removes path traversal and stored-XSS-via-filename entirely, and makes a
     * key collision between two uploads a non-event.
     */
    private String generateStorageKey (MediaScope scope, String extension) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        String randomName = UUID.randomUUID()
                .toString()
                .replace("-", "");
        return "%s/%d/%02d/%s.%s".formatted(
                scope.getPathSegment(),
                today.getYear(),
                today.getMonthValue(),
                randomName,
                extension
                            );
    }

    private String buildPublicUrl (String storageKey) {
        String base = mediaProperties.publicBaseUrl();
        String prefix;
        if (StringUtils.hasText(base)) {
            prefix = stripTrailingSlash(base.trim());
        } else {
            prefix = currentContextRoot();
        }
        return prefix + mediaProperties.publicPath() + "/" + storageKey;
    }

    /**
     * Derives {@code scheme://host/context-path} from the inbound request, so that
     * development behind a proxy on a non-standard port still produces working URLs.
     * Falls back to a relative URL when no request is bound, which is the case for
     * the cleanup sweep.
     */
    private String currentContextRoot () {
        try {
            String root = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                      .build()
                                                      .toUriString();
            return stripTrailingSlash(root);
        } catch (IllegalStateException exception) {
            log.debug("No request bound to this thread; emitting a context-relative media URL.");
            return "";
        }
    }

    private String stripTrailingSlash (String value) {
        String result = value;
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private void writeFile (String storageKey, byte[] content) {
        try {
            mediaStorage.store(storageKey, content);
        } catch (IOException exception) {
            log.error("Failed to write media file at key {}", storageKey, exception);
            throw new BusinessRuleException(
                    "The image could not be stored. Please try again."
            );
        }
    }

    private void compensate (String storageKey, RuntimeException cause) {
        try {
            mediaStorage.delete(storageKey);
        } catch (IOException cleanupFailure) {
            log.warn(
                    "Failed to roll back media file at key {} after a persistence error; "
                            + "the cleanup sweep will remove it.",
                    storageKey,
                    cleanupFailure
            );
        }
    }

    /**
     * Whether a reference points at this application's own media store.
     * <p>
     * Classification is by {@code app.media.public-path} rather than by scheme,
     * because our own public URLs are absolute
     * ({@code http://localhost:8080/api/v1/media/...}) and an "is it absolute?" test
     * would misclassify every legitimate reference as third-party. Anything that
     * carries our public path is ours; everything else is treated as external and
     * gated by {@code app.media.allow-external-urls}.
     */
    private boolean isManagedUrl (String value) {
        String marker = mediaProperties.publicPath() + "/";
        return value.contains(marker);
    }

    /**
     * Strips any directory component and length-caps the client filename purely for
     * display. Never used to build a path.
     */
    private String sanitiseOriginalFilename (String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return null;
        }
        String cleaned = StringUtils.cleanPath(originalFilename.trim());
        int separator = cleaned.lastIndexOf('/');
        if (separator > -1) {
            cleaned = cleaned.substring(separator + 1);
        }
        cleaned = cleaned.replaceAll("[\\p{Cntrl}]", "");
        if (cleaned.isBlank()) {
            return null;
        }
        return cleaned.length() > 255 ? cleaned.substring(0, 255) : cleaned;
    }

    private MediaUploadResponse toResponse (MediaAsset asset) {
        return new MediaUploadResponse(
                asset.getId(),
                asset.getPublicUrl(),
                asset.getStorageKey(),
                asset.getScope(),
                asset.getContentType(),
                asset.getSizeBytes(),
                asset.getWidth(),
                asset.getHeight()
                           );
    }
}