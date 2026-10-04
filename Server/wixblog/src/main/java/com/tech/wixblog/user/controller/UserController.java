package com.tech.wixblog.user.controller;

import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.user.dto.PublicUserProfileResponse;
import com.tech.wixblog.user.dto.UpdateProfileRequest;
import com.tech.wixblog.user.dto.UserMeResponse;
import com.tech.wixblog.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Users",
        description = "The caller's own profile, and public profiles as seen by others."
                )
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get the authenticated user's profile",
            description = "Includes private fields such as email and role."
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getCurrentUser (
            Authentication authentication
                                                        ) {
        UUID userId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(userService.getCurrentUser(userId));
    }

    @Operation(
            summary = "Update the authenticated user's profile",
            description = """
                    Updates display name, bio and avatar.

                    To change the avatar, upload one through POST /media/images?scope=AVATAR
                    first and send the returned URL. The value must be an image this account
                    owns; an external URL, an image belonging to another account, or an image
                    that has since been removed is rejected.

                    This is a full replacement: sending a null `avatarUrl` clears the avatar
                    and deletes the stored file, so send the current URL to keep it.
                    """
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/me/profile")
    public ResponseEntity<UserMeResponse> updateProfile (
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
                                                     ) {
        UUID userId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @Operation(
            summary = "Get a public profile by username",
            description = """
                    Public. When the caller is authenticated the response also reports whether
                    they already follow this user, so the client can render the correct action
                    without a second call.
                    """
    )
    @SecurityRequirement(name = "")
    @GetMapping("/{username}")
    public ResponseEntity<PublicUserProfileResponse> getProfile (
            Authentication authentication,
            @Parameter(description = "Username to look up, matched case-insensitively")
            @PathVariable String username
                                                                      ) {
        UUID viewerId = viewerId(authentication);
        return ResponseEntity.ok(userService.getPublicProfile(username, viewerId));
    }

    /**
     * Returns the caller's identifier, or {@code null} for an anonymous reader. This
     * endpoint serves both, so it must not fail when no principal is present.
     */
    private UUID viewerId (Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return AuthenticatedUser.getId(authentication);
    }
}