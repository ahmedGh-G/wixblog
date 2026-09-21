package com.tech.wixblog.feed.dto;

import java.time.Instant;

public record PersonalizationContext(
        boolean followedAuthor,
        boolean matchingCategory,
        boolean matchingTag,
        long likes,
        long comments,
        Instant publishedAt
) {
}