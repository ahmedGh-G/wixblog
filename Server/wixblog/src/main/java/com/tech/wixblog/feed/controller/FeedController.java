package com.tech.wixblog.feed.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.feed.domain.FeedType;
import com.tech.wixblog.feed.service.I.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Feeds",
        description = "Ranked story feeds: stories from followed authors, personalised "
                + "recommendations, editorially featured stories, and trending stories."
                )
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Stories from authors you follow",
            description = "Newest first. Requires authentication."
    )
    @GetMapping("/following")
    public Page<StorySummaryResponse> following (
            Authentication authentication,
            @ParameterObject
            @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC)
            Pageable pageable
                                                ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        return feedService.getFeed(FeedType.FOLLOWING, userId, pageable);
    }

    @Operation(
            summary = "Personalised recommendations",
            description = "Scored against the caller's reading, following and topic signals. "
                    + "Requires authentication."
    )
    @GetMapping("/for-you")
    public Page<StorySummaryResponse> forYou (
            Authentication authentication,
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
                                             ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        return feedService.getFeed(FeedType.FOR_YOU, userId, pageable);
    }

    @Operation(
            summary = "Featured stories",
            description = "Stories an editor has promoted. Not personalised."
    )
    @GetMapping("/featured")
    public Page<StorySummaryResponse> featured (
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
                                               ) {
        return feedService.getFeed(FeedType.FEATURED, null, pageable);
    }

    @Operation(
            summary = "Trending stories",
            description = "Ranked by recent engagement velocity. Not personalised."
    )
    @GetMapping("/trending")
    public Page<StorySummaryResponse> trending (
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
                                               ) {
        return feedService.getFeed(FeedType.TRENDING, null, pageable);
    }
}