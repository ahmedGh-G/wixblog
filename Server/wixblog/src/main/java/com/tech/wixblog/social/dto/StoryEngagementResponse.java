package com.tech.wixblog.social.dto;

public record StoryEngagementResponse(
        long likeCount,
        long commentCount,
        boolean likedByCurrentUser
) {
}