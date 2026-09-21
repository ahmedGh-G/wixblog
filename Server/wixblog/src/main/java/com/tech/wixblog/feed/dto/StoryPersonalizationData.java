package com.tech.wixblog.feed.dto;

import java.util.UUID;

public record StoryPersonalizationData(
        UUID storyId,
        boolean followedAuthor,
        boolean matchingCategory,
        boolean matchingTag,
        long likes,
        long comments
) {
}