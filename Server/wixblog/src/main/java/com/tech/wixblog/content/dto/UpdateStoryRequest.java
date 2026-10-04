package com.tech.wixblog.content.dto;

import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.validation.MediaReference;
import jakarta.validation.constraints.Size;

import java.util.UUID;
import java.util.Set;

public record UpdateStoryRequest(

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
         * Editor.js document, serialised as JSON text.
         */
        @Size(
                max = 200000,
                message = "Content cannot exceed 200000 characters"
        )
        String content,

        /**
         * URL returned by {@code POST /media/images?scope=STORY_COVER}.
         * <p>
         * This is a full replacement, not a patch: consistent with every other field on
         * this record, a null or blank value clears the cover. Send the current URL to
         * keep it. Replacing a cover retires the previously stored image.
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