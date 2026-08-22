package com.tech.wixblog.auth.dto;

import com.tech.wixblog.user.domain.Role;

import java.util.UUID;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UUID userId,
    String username,
    Role role
) {}