package com.tech.wixblog.social.dto;

import com.tech.wixblog.user.dto.PublicUserResponse;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        String content,
        PublicUserResponse author,
        Instant createdAt,
        Instant updatedAt
) {
}