package com.tech.wixblog.feed.service.strategy;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.feed.domain.FeedType;
import com.tech.wixblog.feed.dto.RankedStory;
import com.tech.wixblog.feed.dto.StoryRankingData;
import com.tech.wixblog.feed.mapper.StorySummaryMapper;
import com.tech.wixblog.feed.service.ranking.StoryRankingContext;
import com.tech.wixblog.feed.service.ranking.StoryScoreCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrendingFeedStrategy implements FeedStrategy {
    private static final int CANDIDATE_LIMIT = 200;
    private final StoryRepository storyRepository;
    private final StorySummaryMapper storySummaryMapper;
    private final StoryScoreCalculator scoreCalculator;

    @Override
    public FeedType getType () {
        return FeedType.TRENDING;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StorySummaryResponse> execute (
            UUID userId,
            Pageable pageable
                                              ) {
        // 1. Get a bounded candidate set.
        List<Story> candidates =
                storyRepository
                        .findRecentPublishedStories(
                                StoryStatus.PUBLISHED,
                                PageRequest.of(0, CANDIDATE_LIMIT)
                                                   )
                        .getContent();
        if (candidates.isEmpty()) {
            return Page.empty(pageable);
        }
        // 2. Get all story IDs.
        List<UUID> storyIds =
                candidates.stream()
                        .map(Story::getId)
                        .toList();
        // 3. Fetch likes/comments for all candidates in one query.
        Map<UUID, StoryRankingData> rankingDataByStoryId =
                storyRepository
                        .findRankingDataByStoryIds(storyIds)
                        .stream()
                        .collect(Collectors.toMap(
                                StoryRankingData::storyId,
                                rankedData -> rankedData // <--- Use this instead of data -> data
                                                 ));
        // 4. Calculate score for every candidate.
        List<RankedStory> rankedStories =
                candidates.stream()
                        .map(story -> {
                            StoryRankingData rankingData =
                                    rankingDataByStoryId.get(story.getId());
                            long likes = rankingData != null
                                    ? rankingData.likes()
                                    : 0;
                            long comments = rankingData != null
                                    ? rankingData.comments()
                                    : 0;
                            StoryRankingContext context =
                                    new StoryRankingContext(
                                            likes,
                                            comments,
                                            story.getPublishedAt()
                                    );
                            double score =
                                    scoreCalculator.calculate(context);
                            return new RankedStory(
                                    story,
                                    score
                            );
                        })
                        .sorted(
                                Comparator
                                        .comparingDouble(
                                                RankedStory::score
                                                        )
                                        .reversed()
                               )
                        .toList();
        // 5. Apply requested page manually.
        int start = (int) pageable.getOffset();
        if (start >= rankedStories.size()) {
            return Page.empty(pageable);
        }
        int end = Math.min(
                start + pageable.getPageSize(),
                rankedStories.size()
                          );
        List<StorySummaryResponse> content =
                rankedStories
                        .subList(start, end)
                        .stream()
                        .map(RankedStory::story)
                        .map(storySummaryMapper::toResponse)
                        .toList();
        return new PageImpl<>(
                content,
                pageable,
                rankedStories.size()
        );
    }
}