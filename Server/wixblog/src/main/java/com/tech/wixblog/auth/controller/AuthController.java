package com.tech.wixblog.auth.controller;

import com.tech.wixblog.auth.dto.LoginRequest;
import com.tech.wixblog.auth.dto.LoginResponse;
import com.tech.wixblog.auth.dto.RegisterRequest;
import com.tech.wixblog.auth.dto.RegisterResponse;
import com.tech.wixblog.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Auth",
        description = "Registration and token issuance. Tokens are stateless; sign out by "
                + "discarding the token client-side."
                )
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register an account",
            description = """
                    Creates the user and an associated profile in one step.

                    Registration is deliberately JSON-only rather than multipart, so a failed
                    avatar never blocks signup. To set an avatar, log in and then call
                    PUT /users/me/profile with a URL from POST /media/images?scope=AVATAR.

                    Passwords must be 8 to 72 characters and contain an uppercase letter, a
                    lowercase letter and a digit.
                    """
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register (
            @Valid @RequestBody RegisterRequest request
                                                    ) {
        RegisterResponse response = authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Exchange credentials for an access token",
            description = "Returns a bearer token valid for 30 minutes. Send it as "
                    + "`Authorization: Bearer <token>` on protected endpoints."
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (
            @Valid @RequestBody LoginRequest request
                                               ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}