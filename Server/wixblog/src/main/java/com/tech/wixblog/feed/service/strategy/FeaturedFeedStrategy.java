package com.tech.wixblog.feed.service.strategy;

import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.feed.domain.FeedType;
import com.tech.wixblog.feed.mapper.StorySummaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeaturedFeedStrategy implements FeedStrategy {
    private final StoryRepository storyRepository;
    private final StorySummaryMapper storySummaryMapper;

    @Override
    public FeedType getType () {
        return FeedType.FEATURED;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StorySummaryResponse> execute (
            UUID userId,
            Pageable pageable
                                              ) {
        return storyRepository
                .findFeaturedStories(
                        StoryStatus.PUBLISHED,
                        pageable
                                    )
                .map(storySummaryMapper::toResponse);
    }
}