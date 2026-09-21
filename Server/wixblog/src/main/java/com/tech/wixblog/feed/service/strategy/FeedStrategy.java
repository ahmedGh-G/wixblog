package com.tech.wixblog.feed.service.strategy;

import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.feed.domain.FeedType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeedStrategy {
    FeedType getType ();

    Page<StorySummaryResponse> execute (
            UUID userId,
            Pageable pageable
                                       );
}