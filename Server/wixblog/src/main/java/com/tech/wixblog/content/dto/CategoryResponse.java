package com.tech.wixblog.content.dto;

import com.tech.wixblog.content.domain.Category;

import java.util.UUID;

public record CategoryResponse(
    UUID id,
    String name,
    String slug
) {

    public static CategoryResponse from(
        Category category
    ) {

        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getSlug()
        );
    }
}