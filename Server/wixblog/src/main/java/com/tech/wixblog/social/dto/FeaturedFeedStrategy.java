package com.tech.wixblog.social.dto;

import com.tech.wixblog.social.domain.FeedItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FeaturedFeedStrategy
        implements FeedStrategy {
    @Override
    public Page<FeedItemResponse> getFeed (UUID userId, Pageable pageable) {
        return null;
    }
}