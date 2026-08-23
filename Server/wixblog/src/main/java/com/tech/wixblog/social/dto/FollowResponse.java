package com.tech.wixblog.social.dto;

import java.util.UUID;

public record FollowResponse(

    UUID followerId,

    UUID followingId,

    boolean following

) {}