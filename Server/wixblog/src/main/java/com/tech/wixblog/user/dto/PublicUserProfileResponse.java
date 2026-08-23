package com.tech.wixblog.user.dto;

import java.util.UUID;

public record PublicUserProfileResponse(

    UUID id,

    String username,

    String displayName,

    String bio,

    String avatarUrl,

    long followersCount,

    long followingCount,

    boolean following

) {}