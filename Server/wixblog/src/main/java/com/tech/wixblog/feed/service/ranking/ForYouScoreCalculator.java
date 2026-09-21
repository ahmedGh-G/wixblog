package com.tech.wixblog.feed.service.ranking;

import com.tech.wixblog.feed.dto.PersonalizationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class ForYouScoreCalculator {
    private static final double FOLLOWED_AUTHOR_WEIGHT = 100.0;
    private static final double CATEGORY_WEIGHT = 50.0;
    private static final double TAG_WEIGHT = 20.0;
    private static final double LIKE_WEIGHT = 3.0;
    private static final double COMMENT_WEIGHT = 5.0;

    public double calculate (PersonalizationContext context) {
        double score = 0.0;
        if (context.followedAuthor()) {
            score += FOLLOWED_AUTHOR_WEIGHT;
        }
        if (context.matchingCategory()) {
            score += CATEGORY_WEIGHT;
        }
        if (context.matchingTag()) {
            score += TAG_WEIGHT;
        }
        score += context.likes() * LIKE_WEIGHT;
        score += context.comments() * COMMENT_WEIGHT;
        long ageHours = Math.max(
                1,
                Duration.between(
                        context.publishedAt(),
                        Instant.now()
                                ).toHours()
                                );
        return score / ageHours;
    }
}