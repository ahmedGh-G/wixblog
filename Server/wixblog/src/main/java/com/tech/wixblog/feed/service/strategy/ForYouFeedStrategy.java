package com.tech.wixblog.feed.service.strategy;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.feed.domain.FeedType;
import com.tech.wixblog.feed.dto.PersonalizationContext;
import com.tech.wixblog.feed.dto.RankedStory;
import com.tech.wixblog.feed.dto.StoryPersonalizationData;
import com.tech.wixblog.feed.mapper.StorySummaryMapper;
import com.tech.wixblog.feed.service.ranking.ForYouScoreCalculator;
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

@Component
@RequiredArgsConstructor
public class ForYouFeedStrategy implements FeedStrategy {
    private static final int CANDIDATE_LIMIT = 200;
    private final StoryRepository storyRepository;
    private final StorySummaryMapper storySummaryMapper;
    private final ForYouScoreCalculator scoreCalculator;
    private final ForYouPersonalizationService personalizationService;

    @Override
    public FeedType getType () {
        return FeedType.FOR_YOU;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StorySummaryResponse> execute (
            UUID userId,
            Pageable pageable
                                              ) {
        // 1. Retrieve a bounded candidate set
        List<Story> candidates = storyRepository
                .findRecentPublishedStories(
                        StoryStatus.PUBLISHED,
                        PageRequest.of(0, CANDIDATE_LIMIT)
                                           )
                .getContent();
        if (candidates.isEmpty()) {
            return Page.empty(pageable);
        }
        // 2. Extract candidate IDs
        List<UUID> storyIds = candidates
                .stream()
                .map(Story::getId)
                .toList();
        // 3. Retrieve personalization data
        Map<UUID, StoryPersonalizationData> personalizationData =
                personalizationService.getPersonalizationData(
                        userId,
                        storyIds
                                                             );
        // 4. Calculate score for every candidate
        List<RankedStory> rankedStories = candidates
                .stream()
                .map(story -> {
                    StoryPersonalizationData data =
                            personalizationData.get(story.getId());
                    if (data == null) {
                        data = new StoryPersonalizationData(
                                story.getId(),
                                false,
                                false,
                                false,
                                0L,
                                0L
                        );
                    }
                    PersonalizationContext context =
                            new PersonalizationContext(
                                    data.followedAuthor(),
                                    data.matchingCategory(),
                                    data.matchingTag(),
                                    data.likes(),
                                    data.comments(),
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
                                .thenComparing(
                                        ranked -> ranked
                                                .story()
                                                .getPublishedAt(),
                                        Comparator.reverseOrder()
                                              )
                       )
                .toList();
        // 5. Apply pagination AFTER ranking
        int start = (int) pageable.getOffset();
        if (start >= rankedStories.size()) {
            return new PageImpl<>(
                    List.of(),
                    pageable,
                    rankedStories.size()
            );
        }
        int end = Math.min(
                start + pageable.getPageSize(),
                rankedStories.size()
                          );
        // 6. Map ranked stories to API responses
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