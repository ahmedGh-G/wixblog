package com.tech.wixblog.feed.dto;

import java.util.UUID;

public interface TrendingTagProjection {
    UUID getTagId ();

    String getTagName ();

    String getTagSlug ();

    long getEngagementCount ();
}