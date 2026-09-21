package com.tech.wixblog.feed.service.ranking;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class TrendingScoreCalculator implements StoryScoreCalculator {
    private static final double LIKE_WEIGHT = 3.0;
    private static final double COMMENT_WEIGHT = 5.0;

    @Override
    public double calculate (StoryRankingContext context) {
        long ageHours = Math.max(
                1,
                Duration.between(
                        context.publishedAt(),
                        Instant.now()
                                ).toHours()
                                );
        double engagement =
                (context.likes() * LIKE_WEIGHT)
                        + (context.comments() * COMMENT_WEIGHT);
        return engagement / ageHours;
    }
}