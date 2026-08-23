package com.tech.wixblog.user.dto;

import com.tech.wixblog.user.domain.Role;
import com.tech.wixblog.user.domain.UserStatus;

import java.util.UUID;

public record UserMeResponse(

    UUID id,

    String email,

    String username,

    String displayName,

    String bio,

    String avatarUrl,

    Role role,

    UserStatus status

) {}