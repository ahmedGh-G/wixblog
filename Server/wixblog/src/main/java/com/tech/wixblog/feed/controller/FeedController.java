package com.tech.wixblog.feed.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.feed.domain.FeedType;
import com.tech.wixblog.feed.service.I.FeedService;
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

@Tag(name = "Feed Engine",
     description = "Endpoints for retrieving Stories feeds of to authenticated user . it covers " +
             "stories of the following users , ForYou feeds , featured  , based on" +
             " the stories ranking and scoring used algorithms")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final AuthenticationService authenticationService;

    @GetMapping("/following")
    public Page<StorySummaryResponse> following (
            Authentication authentication,
            @ParameterObject @PageableDefault(
                    size = 20,
                    sort = "publishedAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
                                                ) {
        UUID userId =
                authenticationService
                        .getAuthenticatedUserId(authentication);
        return feedService.getFeed(
                FeedType.FOLLOWING,
                userId,
                pageable
                                  );
    }

    @GetMapping("/for-you")
    public Page<StorySummaryResponse> forYou (
            Authentication authentication,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                                             ) {
        UUID userId =
                authenticationService
                        .getAuthenticatedUserId(authentication);
        return feedService.getFeed(
                FeedType.FOR_YOU,
                userId,
                pageable
                                  );
    }

    @GetMapping("/featured")
    public Page<StorySummaryResponse> featured (
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                                               ) {
        return feedService.getFeed(
                FeedType.FEATURED,
                null,
                pageable
                                  );
    }

    @GetMapping("/trending")
    public Page<StorySummaryResponse> trending (
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                                               ) {
        return feedService.getFeed(
                FeedType.TRENDING,
                null,
                pageable
                                  );
    }
}