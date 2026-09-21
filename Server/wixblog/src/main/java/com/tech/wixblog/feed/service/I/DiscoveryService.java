package com.tech.wixblog.feed.service.I;

import com.tech.wixblog.content.dto.StorySummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiscoveryService {
    Page<StorySummaryResponse> discoverByTrendingCategories (
            Pageable pageable
                                                            );

    Page<StorySummaryResponse> discoverByTrendingTags (
            Pageable pageable
                                                      );
}