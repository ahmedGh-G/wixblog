package com.tech.wixblog.content.dto;

import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.validation.MediaReference;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record CreateStoryRequest(

        @Size(
                max = 150,
                message = "Title cannot exceed 150 characters"
        )
        String title,

        @Size(
                max = 300,
                message = "Subtitle cannot exceed 300 characters"
        )
        String subtitle,

        /**
         * Editor.js document, serialised as JSON text. Bounded here because the column
         * is {@code text} and therefore effectively unlimited; the limit protects the
         * row, the indexes and the response payload.
         */
        @Size(
                max = 200000,
                message = "Content cannot exceed 200000 characters"
        )
        String content,

        /**
         * URL returned by {@code POST /media/images?scope=STORY_COVER}. Resolved against
         * the media ledger so a cover cannot point at an image the caller does not own
         * or at one that has since been deleted.
         */
        @Size(
                max = 1000,
                message = "Cover image URL cannot exceed 1000 characters"
        )
        @MediaReference(scope = MediaScope.STORY_COVER)
        String coverImageUrl,

        UUID categoryId,

        Set<UUID> tagIds

) {}