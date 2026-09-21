package com.tech.wixblog.feed.dto;

import java.util.UUID;

public record CategoryRecommendationResponse(
        UUID id,
        String name,
        String slug
) {
}