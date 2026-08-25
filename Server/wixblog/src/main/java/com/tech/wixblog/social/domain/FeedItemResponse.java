package com.tech.wixblog.social.domain;

import com.tech.wixblog.content.dto.CategoryResponse;
import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.user.dto.PublicUserResponse;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record FeedItemResponse(
        UUID id,
        String title,
        String subtitle,
        String coverImageUrl,
        PublicUserResponse author,
        CategoryResponse category,
        Set<TagResponse> tags,
        Instant publishedAt,
        Integer readingTimeMinutes,
        long likeCount,
        long commentCount,
        boolean likedByCurrentUser,
        boolean bookmarkedByCurrentUser
) {
}