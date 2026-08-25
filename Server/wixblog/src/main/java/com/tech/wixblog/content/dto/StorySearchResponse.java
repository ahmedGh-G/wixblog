package com.tech.wixblog.content.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record StorySearchResponse(
        UUID id,
        String title,
        String subtitle,
        String coverImageUrl,
        UUID authorId,
        String authorUsername,
        CategoryResponse category,
        Set<TagResponse> tags,
        Instant publishedAt,
        Integer readingTimeMinutes
) {
}