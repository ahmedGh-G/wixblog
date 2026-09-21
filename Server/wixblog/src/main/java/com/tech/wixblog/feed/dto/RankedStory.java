package com.tech.wixblog.feed.dto;

import com.tech.wixblog.content.domain.Story;

public record RankedStory(
        Story story,
        double score
) {
}