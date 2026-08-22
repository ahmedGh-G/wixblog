package com.tech.wixblog.auth.controller;

import com.tech.wixblog.auth.dto.RegisterRequest;
import com.tech.wixblog.auth.dto.RegisterResponse;
import com.tech.wixblog.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(
        AuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
        @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response =
            authenticationService.register(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }
}