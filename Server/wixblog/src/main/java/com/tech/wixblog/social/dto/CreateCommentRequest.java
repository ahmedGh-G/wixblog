package com.tech.wixblog.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Comment content is required.")
        @Size(
                min = 1,
                max = 2000,
                message = "Comment must be between 1 and 2000 characters."
        )
        String content
) {
}