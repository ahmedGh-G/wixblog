package com.tech.wixblog.feed.service.ranking;

public interface StoryScoreCalculator {
    double calculate (StoryRankingContext context);
}