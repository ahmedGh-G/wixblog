package com.tech.wixblog.security;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public final class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static UUID getId(
        Authentication authentication
    ) {

        return UUID.fromString(
            authentication.getName()
        );
    }
}