package com.tech.wixblog.media.controller;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.dto.InlineImageUploadResponse;
import com.tech.wixblog.media.dto.MediaUploadResponse;
import com.tech.wixblog.media.service.MediaService;
import com.tech.wixblog.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

/**
 * Image upload endpoints.
 * <p>
 * Uploads are deliberately decoupled from resource creation. The client uploads an
 * image, receives a URL, and submits that URL in the JSON body of the ordinary create
 * or update call. This keeps every existing request and response contract intact and
 * means a story can be created before its cover has finished uploading.
 * <p>
 * Validation failures are raised as common exceptions and translated centrally by
 * {@code GlobalExceptionHandler}, so no error handling is duplicated here.
 */
@Tag(
        name = "Media",
        description = "Image upload and serving. Upload an image, then submit the returned URL "
                + "as the coverImageUrl or avatarUrl of a story or profile."
                )
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @Operation(
            summary = "Upload an image for a cover or avatar",
            description = """
                    Stores an image and returns its descriptor. Submit the returned `url` as the
                    `coverImageUrl` of a story, or the `avatarUrl` of a profile.

                    The file is judged by its own bytes, not by the declared Content-Type: the
                    format is detected from the magic number, the image must decode successfully,
                    and it must fall within the size limit for the requested scope. Accepted images
                    are re-encoded, which strips EXIF metadata including GPS coordinates.

                    Storage keys are generated server-side, so the original filename never reaches
                    the filesystem.
                    """
    )
    @PostMapping(
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MediaUploadResponse> uploadImage (
            Authentication authentication,
            @Parameter(description = "Image file. JPEG, PNG or WebP.")
            @RequestPart("file") MultipartFile file,
            @Parameter(
                    description = "What the image is for. Inline images must use "
                            + "POST /media/inline-images, which returns an Editor.js block."
            )
            @RequestParam("scope") MediaScope scope
                                          ) {
        requireNonInlineScope(scope);
        UUID ownerId = AuthenticatedUser.getId(authentication);

        MediaUploadResponse response = mediaService.store(file, scope, ownerId);
        return ResponseEntity
                .created(URI.create(response.url()))
                .body(response);
    }

    @Operation(
            summary = "Upload an image for embedding inside a story body",
            description = """
                    Stores an image and returns an Editor.js image block that can be inserted into
                    the story document as-is, using `blockId` as the block's id.

                    Only the uploader may subsequently reference this image from a story body.
                    Story content is checked on create, update and publish, so referencing another
                    user's image is rejected.
                    """
    )
    @PostMapping(
            value = "/inline-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public InlineImageUploadResponse uploadInlineImage (
            Authentication authentication,
            @Parameter(description = "Image file. JPEG, PNG, WebP or animated GIF.")
            @RequestPart("file") MultipartFile file
                                                       ) {
        UUID ownerId = AuthenticatedUser.getId(authentication);
        return mediaService.storeInlineImage(file, ownerId);
    }

    /**
     * {@code INLINE_IMAGE} is only reachable through its dedicated endpoint, so the
     * response shape for inline images is never confused with the generic one. The value
     * remains a legal enum constant, hence the explicit rejection rather than relying on
     * the binder to exclude it.
     */
    private void requireNonInlineScope (MediaScope scope) {
        if (scope == MediaScope.INLINE_IMAGE) {
            throw new BusinessRuleException(
                    "INLINE_IMAGE is not accepted by this endpoint. Use POST /media/inline-images."
            );
        }
    }
}