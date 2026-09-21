package com.tech.wixblog.auth.controller;

import com.tech.wixblog.auth.dto.LoginRequest;
import com.tech.wixblog.auth.dto.LoginResponse;
import com.tech.wixblog.auth.dto.RegisterRequest;
import com.tech.wixblog.auth.dto.RegisterResponse;
import com.tech.wixblog.auth.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth",
     description = "Endpoints for managing user authentication and authorization")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController (
            AuthenticationService authenticationService
                          ) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register (
            @Valid @RequestBody RegisterRequest request
                                                     ) {
        RegisterResponse response =
                authenticationService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (
            @Valid @RequestBody LoginRequest request
                                               ) {
        LoginResponse response =
                authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
}