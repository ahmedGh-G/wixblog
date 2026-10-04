package com.tech.wixblog.social.controller;

import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.social.dto.FollowResponse;
import com.tech.wixblog.social.dto.FollowStatusResponse;
import com.tech.wixblog.social.dto.SocialStatsResponse;
import com.tech.wixblog.social.dto.SocialUserResponse;
import com.tech.wixblog.social.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(
        name = "Follows",
        description = "Follow graph between users, plus follower listings and social counts."
                )
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @Operation(
            summary = "Follow a user",
            description = "Adds the caller to the user's followers. Idempotent."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> follow (
            Authentication authentication,
            @Parameter(description = "Identifier of the user to follow")
            @PathVariable UUID userId
                                                 ) {
        UUID followerId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(followService.follow(followerId, userId));
    }

    @Operation(
            summary = "Unfollow a user",
            description = "Removes the caller from the user's followers. Idempotent."
    )
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<FollowResponse> unfollow (
            Authentication authentication,
            @Parameter(description = "Identifier of the user to unfollow")
            @PathVariable UUID userId
                                                   ) {
        UUID followerId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(followService.unfollow(followerId, userId));
    }

    @Operation(
            summary = "List a user's followers",
            description = "Public."
    )
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<SocialUserResponse>> getFollowers (
            @Parameter(description = "Identifier of the followed user")
            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
                                                                 ) {
        return ResponseEntity.ok(followService.getFollowers(userId, pageable));
    }

    @Operation(
            summary = "List the users a user follows",
            description = "Public."
    )
    @GetMapping("/{userId}/following")
    public ResponseEntity<Page<SocialUserResponse>> getFollowing (
            @Parameter(description = "Identifier of the following user")
            @PathVariable UUID userId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
                                                                 ) {
        return ResponseEntity.ok(followService.getFollowing(userId, pageable));
    }

    @Operation(
            summary = "Check whether the caller follows a user",
            description = "Returns the relationship rather than a bare boolean, so the client "
                    + "can render the correct call to action."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<FollowStatusResponse> getFollowStatus (
            Authentication authentication,
            @Parameter(description = "Identifier of the user to check")
            @PathVariable UUID userId
                                                                ) {
        UUID currentUserId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(followService.getFollowStatus(currentUserId, userId));
    }

    @Operation(
            summary = "Get a user's follower and following counts",
            description = "Public."
    )
    @GetMapping("/{userId}/social-stats")
    public ResponseEntity<SocialStatsResponse> getSocialStats (
            @Parameter(description = "Identifier of the user")
            @PathVariable UUID userId
                                                              ) {
        return ResponseEntity.ok(followService.getSocialStats(userId));
    }
}