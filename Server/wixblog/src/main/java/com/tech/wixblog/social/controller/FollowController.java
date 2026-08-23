package com.tech.wixblog.social.controller;

import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.social.dto.FollowResponse;
import com.tech.wixblog.social.service.FollowService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class FollowController {

    private final FollowService followService;

    public FollowController(
        FollowService followService
    ) {
        this.followService = followService;
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> follow(
        Authentication authentication,
        @PathVariable UUID userId
    ) {

        UUID followerId =
            AuthenticatedUser.getId(
                authentication
            );

        return ResponseEntity.ok(
            followService.follow(
                followerId,
                userId
            )
        );
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> unfollow(
        Authentication authentication,
        @PathVariable UUID userId
    ) {

        UUID followerId =
            AuthenticatedUser.getId(
                authentication
            );

        return ResponseEntity.ok(
            followService.unfollow(
                followerId,
                userId
            )
        );
    }
}