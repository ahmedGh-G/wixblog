package com.tech.wixblog.social.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.social.service.FeedService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final AuthenticationService authenticationService;

    @GetMapping("/following")
    public ResponseEntity<Page<StoryResponse>> getFollowingFeed (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
                                                                ) {
        UUID userId =
                authenticationService
                        .getAuthenticatedUserId(authentication);
        Pageable pageable =
                PageRequest.of(
                        page,
                        Math.min(size, 50)
                              );
        return ResponseEntity.ok(
                feedService.getFollowingFeed(
                        userId,
                        pageable
                                            )
                                );
    }
}