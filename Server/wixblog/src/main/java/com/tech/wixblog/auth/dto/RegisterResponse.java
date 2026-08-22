package com.tech.wixblog.auth.dto;

import com.tech.wixblog.user.domain.Role;
import com.tech.wixblog.user.domain.User;

import java.util.UUID;

public record RegisterResponse(
    UUID id,
    String email,
    String username,
    Role role
) {

    public static RegisterResponse from(User user) {
        return new RegisterResponse(
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getRole()
        );
    }
}