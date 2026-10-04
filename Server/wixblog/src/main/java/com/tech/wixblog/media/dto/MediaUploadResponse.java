package com.tech.wixblog.media.dto;

import com.tech.wixblog.media.domain.MediaScope;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Descriptor of a stored image, to be submitted back as the "
        + "coverImageUrl / avatarUrl field of the owning resource.")
public record MediaUploadResponse(

        @Schema(description = "Ledger identifier of the stored asset.")
        UUID id,

        @Schema(description = "Absolute URL to store on the owning resource.")
        String url,

        @Schema(description = "Storage-relative key. Informational; clients should "
                + "submit `url`, not this value.")
        String key,

        @Schema(description = "Scope the image was uploaded for.")
        MediaScope scope,

        @Schema(description = "Content type detected from the file's magic bytes.")
        String contentType,

        @Schema(description = "Stored size in bytes, after any re-encoding.")
        long sizeBytes,

        @Schema(description = "Pixel width, or null when it could not be determined.")
        Integer width,

        @Schema(description = "Pixel height, or null when it could not be determined.")
        Integer height
) {

    public static MediaUploadResponse from (
            UUID id,
            String url,
            String key,
            MediaScope scope,
            String contentType,
            long sizeBytes,
            Integer width,
            Integer height
                                         ) {
        return new MediaUploadResponse(id, url, key, scope, contentType, sizeBytes, width, height);
    }
}