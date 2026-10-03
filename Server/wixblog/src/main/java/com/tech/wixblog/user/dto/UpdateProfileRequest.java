package com.tech.wixblog.user.dto;

import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.validation.MediaReference;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(

        @NotBlank(message = "Display name is required")
        @Size(
                min = 2,
                max = 50,
                message = "Display name must contain 2 to 50 characters"
        )
        String displayName,

        /**
         * Bounded to 500 characters here, matching the {@code user_profiles.bio} column.
         * Previously this was capped at 160 while the column allowed 5000, so a value
         * accepted by the database could still be rejected by the API and vice versa.
         */
        @Size(
                max = 500,
                message = "Bio cannot exceed 500 characters"
        )
        String bio,

        /**
         * URL returned by {@code POST /media/images?scope=AVATAR}.
         * <p>
         * Checked against the media ledger so an avatar cannot point at an image the
         * caller does not own, at an inline image, or at one that has been deleted.
         * Submitting a different avatar retires the previously stored file.
         */
        @Size(
                max = 1000,
                message = "Avatar URL cannot exceed 1000 characters"
        )
        @MediaReference(scope = MediaScope.AVATAR)
        String avatarUrl

) {}