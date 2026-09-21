package com.tech.wixblog.feed.service.strategy;

import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.feed.domain.FeedType;
import com.tech.wixblog.feed.service.I.FeedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl implements FeedService {
    private final Map<FeedType, FeedStrategy> strategies;

    public FeedServiceImpl (
            List<FeedStrategy> strategyList
                           ) {
        this.strategies =
                strategyList.stream()
                        .collect(
                                Collectors.toUnmodifiableMap(
                                        FeedStrategy::getType,
                                        Function.identity()
                                                            )
                                );
    }

    @Override
    public Page<StorySummaryResponse> getFeed (
            FeedType type,
            UUID userId,
            Pageable pageable
                                              ) {
        FeedStrategy strategy =
                strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Unsupported feed type: " + type
            );
        }
        return strategy.execute(
                userId,
                pageable
                               );
    }
}