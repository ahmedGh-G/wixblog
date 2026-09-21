package com.tech.wixblog.feed.dto;

import java.util.UUID;

public interface CategoryRecommendationProjection {
    UUID getId ();

    String getName ();

    String getSlug ();
}