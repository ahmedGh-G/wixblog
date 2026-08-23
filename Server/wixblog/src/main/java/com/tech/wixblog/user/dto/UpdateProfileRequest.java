package com.tech.wixblog.user.dto;

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

    @Size(
        max = 160,
        message = "Bio cannot exceed 160 characters"
    )
    String bio,

    @Size(
        max = 500,
        message = "Avatar URL cannot exceed 500 characters"
    )
    String avatarUrl

) {}