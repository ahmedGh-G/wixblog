package com.tech.wixblog.content.dto;

import com.tech.wixblog.content.domain.StoryStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record StorySummaryResponse(
        UUID id,
        UUID authorId,
        String authorUsername,
        String title,
        String subtitle,
        String coverImageUrl,
        StoryStatus status,
        CategoryResponse category,
        Set<TagResponse> tags,
        Integer readingTimeMinutes,
        Instant publishedAt
) {
}