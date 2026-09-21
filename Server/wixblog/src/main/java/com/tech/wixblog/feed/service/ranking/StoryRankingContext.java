package com.tech.wixblog.feed.service.ranking;

import java.time.Instant;

public record StoryRankingContext(
        long likes,
        long comments,
        Instant publishedAt
) {
}