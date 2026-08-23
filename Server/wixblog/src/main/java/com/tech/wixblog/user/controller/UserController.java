package com.tech.wixblog.user.controller;

import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.user.dto.UpdateProfileRequest;
import com.tech.wixblog.user.dto.UserMeResponse;
import com.tech.wixblog.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(
        UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getCurrentUser(
        Authentication authentication
    ) {

        var userId =
            AuthenticatedUser.getId(authentication);

        return ResponseEntity.ok(
            userService.getCurrentUser(userId)
        );
    }


    @PutMapping("/me/profile")
    public ResponseEntity<UserMeResponse> updateProfile(
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
}