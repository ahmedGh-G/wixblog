package com.tech.wixblog.user.controller;

import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.user.dto.PublicUserProfileResponse;
import com.tech.wixblog.user.dto.UpdateProfileRequest;
import com.tech.wixblog.user.dto.UserMeResponse;
import com.tech.wixblog.user.service.UserSearchService;
import com.tech.wixblog.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserSearchService userSearchService;

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getCurrentUser (
            Authentication authentication
                                                         ) {
        var userId =
                AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(
                userService.getCurrentUser(userId)
                                );
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserMeResponse> updateProfile (
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
                                                        ) {
        var userId =
                AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(
                userService.updateProfile(
                        userId,
                        request
                                         )
                                );
    }

    @GetMapping("/{username}")
    public ResponseEntity<PublicUserProfileResponse> getProfile (
            Authentication authentication,
            @PathVariable String username
                                                                ) {
        UUID viewerId = null;
        if (authentication != null &&
                authentication.isAuthenticated()) {
            viewerId =
                    AuthenticatedUser.getId(
                            authentication
                                           );
        }
        return ResponseEntity.ok(
                userService.getPublicProfile(
                        username,
                        viewerId
                                            )
                                );
    }
}