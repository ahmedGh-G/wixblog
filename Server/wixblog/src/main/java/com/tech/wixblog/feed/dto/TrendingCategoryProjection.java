package com.tech.wixblog.feed.dto;

import java.util.UUID;

public interface TrendingCategoryProjection {
    UUID getCategoryId ();

    String getCategoryName ();

    String getCategorySlug ();

    long getEngagementCount ();
}