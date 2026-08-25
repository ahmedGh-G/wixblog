package com.tech.wixblog.social.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentProjection(
        UUID id,
        String content,
        UUID authorId,
        String username,
        String displayName,
        String bio,
        String avatarUrl,
        Instant createdAt,
        Instant updatedAt
) {
}