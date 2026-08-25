package com.tech.wixblog.content.dto;

import com.tech.wixblog.user.dto.PublicUserResponse;

import java.util.List;

public record GlobalSearchResponse(
        List<StorySearchResponse> stories,
        List<PublicUserResponse> users,
        List<TagResponse> tags
) {
}