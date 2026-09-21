package com.tech.wixblog.feed.dto;

import java.util.UUID;

public interface AuthorRecommendationProjection {
    UUID getUserId ();

    String getUsername ();

    String getDisplayName ();

    String getBio ();

    String getAvatarUrl ();
}