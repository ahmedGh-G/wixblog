package com.tech.wixblog.feed.dto;

import java.util.UUID;

public record AuthorRecommendationResponse(
        UUID userId,
        String username,
        String displayName,
        String bio,
        String avatarUrl
) {
}