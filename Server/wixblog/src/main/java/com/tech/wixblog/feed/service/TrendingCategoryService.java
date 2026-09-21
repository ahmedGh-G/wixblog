package com.tech.wixblog.feed.service;

import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.feed.dto.TrendingCategoryProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendingCategoryService {
    private final StoryRepository storyRepository;

    public Set<UUID> getTrendingCategoryIds (int limit) {
        return storyRepository
                .findTrendingCategories(
                        StoryStatus.PUBLISHED,
                        PageRequest.of(0, limit)
                                       )
                .stream()
                .map(TrendingCategoryProjection::getCategoryId)
                .collect(Collectors.toSet());
    }
}