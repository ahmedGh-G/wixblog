package com.tech.wixblog.social.controller;

import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.social.dto.FollowResponse;
import com.tech.wixblog.social.dto.FollowStatusResponse;
import com.tech.wixblog.social.dto.SocialStatsResponse;
import com.tech.wixblog.social.dto.SocialUserResponse;
import com.tech.wixblog.social.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;



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

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<SocialUserResponse>> getFollowers (
            @PathVariable UUID userId,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
                                                                 ) {

        return ResponseEntity.ok(
                followService.getFollowers(
                        userId,
                        pageable
                                          )
                                );
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<SocialUserResponse>> getFollowing(
            @PathVariable UUID userId,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
                                                                ) {

        return ResponseEntity.ok(
                followService.getFollowing(
                        userId,
                        pageable
                                          )
                                );
    }

    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<FollowStatusResponse> getFollowStatus (
            Authentication authentication,
            @PathVariable UUID userId
                                                                ) {

        UUID currentUserId =
                AuthenticatedUser.getId(authentication);

        return ResponseEntity.ok(
                followService.getFollowStatus(
                        currentUserId,
                        userId
                                             )
                                );
    }


    @GetMapping("/{userId}/social-stats")
    public ResponseEntity<SocialStatsResponse> getSocialStats (
            @PathVariable UUID userId
                                                              ) {

        return ResponseEntity.ok(
                followService.getSocialStats(userId)
                                );
    }


}