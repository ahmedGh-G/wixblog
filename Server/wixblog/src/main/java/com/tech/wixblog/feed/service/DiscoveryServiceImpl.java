package com.tech.wixblog.feed.service;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.content.repository.StorySpecifications;
import com.tech.wixblog.feed.mapper.StorySummaryMapper;
import com.tech.wixblog.feed.service.I.DiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscoveryServiceImpl implements DiscoveryService {
    private static final int TRENDING_CATEGORY_LIMIT = 5;
    private static final int TRENDING_TAG_LIMIT = 10;
    private final StoryRepository storyRepository;
    private final StorySummaryMapper storySummaryMapper;
    private final TrendingCategoryService trendingCategoryService;
    private final TrendingTagService trendingTagService;

    @Override
    public Page<StorySummaryResponse> discoverByTrendingCategories (
            Pageable pageable
                                                                   ) {
        Set<UUID> trendingCategoryIds =
                trendingCategoryService.getTrendingCategoryIds(
                        TRENDING_CATEGORY_LIMIT
                                                              );
        if (trendingCategoryIds.isEmpty()) {
            return Page.empty(pageable);
        }
        Specification<Story> specification =
                StorySpecifications.published()
                        .and(
                                StorySpecifications.hasCategoryIn(
                                        trendingCategoryIds
                                                                 )
                            );
        return storyRepository
                .findAll(specification, pageable)
                .map(storySummaryMapper::toResponse);
    }

    @Override
    public Page<StorySummaryResponse> discoverByTrendingTags (
            Pageable pageable
                                                             ) {
        Set<UUID> trendingTagIds =
                trendingTagService.getTrendingTagIds(
                        TRENDING_TAG_LIMIT
                                                    );
        if (trendingTagIds.isEmpty()) {
            return Page.empty(pageable);
        }
        Specification<Story> specification =
                StorySpecifications.published()
                        .and(
                                StorySpecifications.hasTagIn(
                                        trendingTagIds
                                                            )
                            );
        return storyRepository
                .findAll(specification, pageable)
                .map(storySummaryMapper::toResponse);
    }
}