package com.tech.wixblog.feed.controller;

import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.feed.service.I.DiscoveryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Discovery Api",
     description = "Endpoints stories discovery based on trending categories or tags.")
@RestController
@RequestMapping("/discovery/stories")
@RequiredArgsConstructor
public class DiscoveryController {
    private final DiscoveryService discoveryService;

    @GetMapping("/trending/categories")
    public ResponseEntity<Page<StorySummaryResponse>>
    getStoriesByTrendingCategories (
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                                   ) {
        return ResponseEntity.ok(
                discoveryService.discoverByTrendingCategories(pageable)
                                );
    }

    @GetMapping("/trending/tags")
    public ResponseEntity<Page<StorySummaryResponse>>
    getStoriesByTrendingTags (
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                             ) {
        return ResponseEntity.ok(
                discoveryService.discoverByTrendingTags(pageable)
                                );
    }
}