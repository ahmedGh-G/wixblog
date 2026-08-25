package com.tech.wixblog.social.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotBlank(message = "Comment content cannot be empty.")
        @Size(max = 2000, message = "Comment content must be under 2000 characters.")
        String content
) {
}
