package com.tech.wixblog.feed.dto;

import java.util.UUID;

public record StoryRankingData(
        UUID storyId,
        long likes,
        long comments
) {
}