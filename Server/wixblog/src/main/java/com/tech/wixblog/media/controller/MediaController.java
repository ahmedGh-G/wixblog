package com.tech.wixblog.media.controller;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.dto.InlineImageUploadResponse;
import com.tech.wixblog.media.dto.MediaUploadResponse;
import com.tech.wixblog.media.service.MediaService;
import com.tech.wixblog.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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
 */
@Tag(
        name = "Media",
        description = "Image upload and serving. Upload an image, then submit the "
                + "returned URL as the coverImageUrl or avatarUrl of a story or profile."
                )
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Validated
public class MediaController {

    private final MediaService mediaService;

    @Operation(
            summary = "Upload an image for a cover or avatar",
            description = """
                    Stores an image and returns its descriptor. Submit the returned `url` as the
                    `coverImageUrl` field of a story, or the `avatarUrl` field of a profile.

                    The file is validated by inspecting its own bytes, not the declared
                    Content-Type: the format is detected from the file's magic number, the
                    image must decode successfully, and it must fall within the size limit
                    for the requested scope.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Image stored successfully."),
            @ApiResponse(responseCode = "400", description = "The file part is missing or the request is malformed."),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token."),
            @ApiResponse(responseCode = "413", description = "The file exceeds the size limit for this scope."),
            @ApiResponse(responseCode = "415", description = "The file is not an accepted image format for this scope.")
    })
    @PostMapping(
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MediaUploadResponse> uploadImage (
            Authentication authentication,

            @Parameter(
                    description = "The image file.",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart("file") MultipartFile file,

            @Parameter(
                    description = "What the image is for. Only AVATAR and STORY_COVER are "
                            + "accepted here; inline images use POST /media/inline-images."
            )
            @RequestParam("scope") @NotNull MediaScope scope
                                                   ) {
        UUID ownerId = AuthenticatedUser.getId(authentication);
        requireNonInlineScope(scope);

        MediaUploadResponse response = mediaService.store(file, scope, ownerId);
        return ResponseEntity
                .created(URI.create(response.url()))
                .body(response);
    }

    @Operation(
            summary = "Upload an image for embedding inside a story body",
            description = """
                    Stores an image and returns an Editor.js image block that can be inserted
                    into the story document as-is.

                    Only the authenticated uploader may later reference this image from a story
                    body; see the content validation performed on story create, update and publish.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Image stored successfully."),
            @ApiResponse(responseCode = "400", description = "The file part is missing or the request is malformed."),
            @ApiResponse(responseCode = "401", description = "Missing or invalid bearer token."),
            @ApiResponse(responseCode = "413", description = "The file exceeds the inline image size limit."),
            @ApiResponse(responseCode = "415", description = "The file is not an accepted image format.")
    })
    @PostMapping(
            value = "/inline-images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<InlineImageUploadResponse> uploadInlineImage (
            Authentication authentication,

            @Parameter(
                    description = "The image file.",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart("file") MultipartFile file
                                                       ) {
        UUID ownerId = AuthenticatedUser.getId(authentication);
        InlineImageUploadResponse response = mediaService.storeInlineImage(file, ownerId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * {@code INLINE_IMAGE} is only reachable through its dedicated endpoint, so that
     * the response shape for inline images is never confused with the generic one.
     * The value is still a legal enum constant, hence the explicit rejection rather
     * than relying on the binder to exclude it.
     */
    private void requireNonInlineScope (MediaScope scope) {
        if (scope == MediaScope.INLINE_IMAGE) {
            throw new BusinessRuleException(
                    "INLINE_IMAGE is not accepted by this endpoint. Use POST /media/inline-images."
            );
        }
    }
}