package com.tech.wixblog.feed.controller;

import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.feed.service.I.DiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(
        name = "Discovery",
        description = "Story discovery driven by what is trending across categories and tags."
                )
@RestController
@RequestMapping("/discovery/stories")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @Operation(
            summary = "Discover stories through trending categories",
            description = "Surfaces recent stories from the categories currently attracting the "
                    + "most engagement."
    )
    @GetMapping("/trending/categories")
    public ResponseEntity<Page<StorySummaryResponse>> getStoriesByTrendingCategories (
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
                                   ) {
        return ResponseEntity.ok(discoveryService.discoverByTrendingCategories(pageable));
    }

    @Operation(
            summary = "Discover stories through trending tags",
            description = "Surfaces recent stories carrying the tags currently attracting the "
                    + "most engagement."
    )
    @GetMapping("/trending/tags")
    public ResponseEntity<Page<StorySummaryResponse>> getStoriesByTrendingTags (
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
                            ) {
        return ResponseEntity.ok(discoveryService.discoverByTrendingTags(pageable));
    }
}