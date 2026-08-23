package com.tech.wixblog.social.dto;

public record SocialStatsResponse(
        long followersCount,
        long followingCount
) {
}