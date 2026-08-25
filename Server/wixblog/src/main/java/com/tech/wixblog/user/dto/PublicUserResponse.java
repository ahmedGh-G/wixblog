package com.tech.wixblog.user.dto;

import java.util.UUID;

public record PublicUserResponse(

        UUID id,

        String username,

        String displayName,

        String bio,

        String avatarUrl

) {
}