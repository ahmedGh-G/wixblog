package com.tech.wixblog.social.dto;

import com.tech.wixblog.social.domain.FeedItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeedStrategy {
    Page<FeedItemResponse> getFeed (UUID userId, Pageable pageable);
}