package com.tech.wixblog.media.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Response of {@code POST /media/inline-images}.
 * <p>
 * Shaped so the Angular editor can insert {@link #data()} into the Editor.js
 * document verbatim and use {@link #blockId()} as the block's {@code id}, which is
 * what Editor.js itself does for an image block. No client-side reshaping, and no
 * second round trip to learn the image's dimensions.
 */
@Schema(description = "Editor.js image block for an inline story image.")
public record InlineImageUploadResponse(

        @Schema(description = "Identifier to assign to the created Editor.js block.")
        String blockId,

        @Schema(description = "Absolute URL of the stored image.")
        String url,

        @Schema(description = "Storage-relative key. Informational only.")
        String key,

        @Schema(description = "Pixel width of the image.")
        int width,

        @Schema(description = "Pixel height of the image.")
        int height,

        @Schema(description = "Stored size in bytes.")
        long sizeBytes,

        @Schema(description = "The Editor.js image block payload, ready to insert.")
        EditorJsImageData data
) {

    /**
     * Editor.js image block {@code data} object.
     * <p>
     * {@code file} uses the object form rather than the bare string form so that
     * {@code name} can be populated later without a breaking API change.
     */
    @Schema(description = "Editor.js image block data.")
    public record EditorJsImageData(
            EditorJsFile file,
            int width,
            int height,
            boolean withBorder,
            boolean withBackground,
            String caption
    ) {}

    @Schema(description = "Editor.js image file reference.")
    public record EditorJsFile(
            String url,
            String name
    ) {}

    public static InlineImageUploadResponse forEditorJs (MediaUploadResponse upload) {
        String blockId = UUID.randomUUID().toString();
        EditorJsFile file = new EditorJsFile(upload.url(), null);
        EditorJsImageData data = new EditorJsImageData(
                file,
                upload.width() == null ? 0 : upload.width(),
                upload.height() == null ? 0 : upload.height(),
                false,
                false,
                ""
                                    );
        return new InlineImageUploadResponse(
                blockId,
                upload.url(),
                upload.key(),
                data.width(),
                data.height(),
                upload.sizeBytes(),
                data
                            );
    }
}