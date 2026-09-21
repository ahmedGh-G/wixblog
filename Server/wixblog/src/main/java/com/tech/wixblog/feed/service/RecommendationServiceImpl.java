package com.tech.wixblog.feed.service;

import com.tech.wixblog.feed.dto.AuthorRecommendationProjection;
import com.tech.wixblog.feed.dto.AuthorRecommendationResponse;
import com.tech.wixblog.feed.dto.CategoryRecommendationProjection;
import com.tech.wixblog.feed.dto.CategoryRecommendationResponse;
import com.tech.wixblog.feed.repository.RecommendationRepository;
import com.tech.wixblog.feed.service.I.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;

    @Override
    public List<AuthorRecommendationResponse> recommendAuthors (
            UUID userId,
            int limit
                                                               ) {
        validateLimit(limit);
        return recommendationRepository
                .findRecommendedAuthors(userId)
                .stream()
                .limit(limit)
                .map(this::toAuthorResponse)
                .toList();
    }

    @Override
    public List<CategoryRecommendationResponse> recommendCategories (
            UUID userId,
            int limit
                                                                    ) {
        validateLimit(limit);
        return recommendationRepository
                .findRecommendedCategories()
                .stream()
                .limit(limit)
                .map(this::toCategoryResponse)
                .toList();
    }

    private AuthorRecommendationResponse toAuthorResponse (
            AuthorRecommendationProjection projection
                                                          ) {
        return new AuthorRecommendationResponse(
                projection.getUserId(),
                projection.getUsername(),
                projection.getDisplayName(),
                projection.getBio(),
                projection.getAvatarUrl()
        );
    }

    private CategoryRecommendationResponse toCategoryResponse (
            CategoryRecommendationProjection projection
                                                              ) {
        return new CategoryRecommendationResponse(
                projection.getId(),
                projection.getName(),
                projection.getSlug()
        );
    }

    private void validateLimit (int limit) {
        if (limit < 1 || limit > 50) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 50"
            );
        }
    }
}