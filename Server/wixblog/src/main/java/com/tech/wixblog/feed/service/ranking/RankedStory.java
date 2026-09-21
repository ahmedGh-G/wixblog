package com.tech.wixblog.feed.service.ranking;

import com.tech.wixblog.content.domain.Story;

public record RankedStory(
        Story story,
        double score
) {
}