package com.tech.wixblog.feed.service.I;

import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.feed.domain.FeedType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeedService {
    Page<StorySummaryResponse> getFeed (
            FeedType feedType,
            UUID userId,
            Pageable pageable
                                       );
}