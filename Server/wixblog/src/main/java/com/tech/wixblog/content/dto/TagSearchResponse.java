package com.tech.wixblog.content.dto;

import com.tech.wixblog.content.domain.Tag;

import java.util.UUID;

public record TagSearchResponse(

        UUID id,

        String name,

        String slug

) {

    public static TagSearchResponse from(
            Tag tag
                                        ) {

        return new TagSearchResponse(
                tag.getId(),
                tag.getName(),
                tag.getSlug()
        );
    }
}