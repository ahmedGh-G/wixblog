package com.tech.wixblog.media.validation;

import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.repository.MediaAssetRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MediaReferenceValidator
        implements ConstraintValidator<MediaReference, String> {

    private final MediaAssetRepository mediaAssetRepository;
    private final MediaProperties mediaProperties;

    private MediaScope requiredScope = MediaScope.STORY_COVER;
    private boolean externalAllowed;

    @Override
    public void initialize (MediaReference constraint) {
        this.requiredScope = constraint.scope();
        this.externalAllowed = constraint.allowExternal();
    }

    @Override
    public boolean isValid (String value, ConstraintValidatorContext context) {
        // Absent is valid; requiredness is expressed with @NotNull/@NotBlank where needed.
        if (!StringUtils.hasText(value)) {
            return true;
        }
        String candidate = value.trim();

        /*
         * Classification is by our public path, not by scheme: our own URLs are
         * absolute (http://host/api/v1/media/...), so an "is it absolute?" test would
         * reject every legitimate reference as if it were third-party.
         */
        if (!isManagedUrl(candidate)) {
            boolean allowed = externalAllowed || mediaProperties.allowExternalUrls();
            if (allowed) {
                return true;
            }
            reject(
                    context,
                    "Only images uploaded through this application may be used here."
                            );
            return false;
        }

        Optional<MediaAsset> asset = mediaAssetRepository.findByPublicUrl(candidate);
        if (asset.isEmpty()) {
            reject(context, "This image was not uploaded through this application.");
            return false;
        }
        MediaAsset found = asset.get();
        if (found.isDeleted()) {
            reject(context, "This image has been removed and can no longer be used.");
            return false;
        }
        if (found.getScope() != requiredScope) {
            reject(
                    context,
                    "This image was uploaded for %s and cannot be used here."
                            .formatted(describe(requiredScope))
                );
            return false;
        }
        return true;
    }

    private String describe (MediaScope scope) {
        return switch (scope) {
            case AVATAR -> "a profile picture";
            case STORY_COVER -> "a story cover";
            case INLINE_IMAGE -> "an inline story image";
        };
    }

    private void reject (ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();
    }

    /**
     * True when the reference points inside this application's own media store,
     * whether expressed absolutely or relative to the context path.
     */
    private boolean isManagedUrl (String value) {
        String marker = mediaProperties.publicPath() + "/";
        return value.contains(marker);
    }
}