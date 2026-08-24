package com.tech.wixblog.content.dto;

import com.tech.wixblog.content.domain.Tag;

import java.util.UUID;

public record TagResponse(
    UUID id,
    String name,
    String slug
) {

    public static TagResponse from(
        Tag tag
    ) {

        return new TagResponse(
            tag.getId(),
            tag.getName(),
            tag.getSlug()
        );
    }
}