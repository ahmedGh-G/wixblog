package com.tech.wixblog.feed.service.I;

import com.tech.wixblog.feed.dto.AuthorRecommendationResponse;
import com.tech.wixblog.feed.dto.CategoryRecommendationResponse;

import java.util.List;
import java.util.UUID;

public interface RecommendationService {
    List<AuthorRecommendationResponse> recommendAuthors (
            UUID userId,
            int limit
                                                        );

    List<CategoryRecommendationResponse> recommendCategories (
            UUID userId,
            int limit
                                                             );
}