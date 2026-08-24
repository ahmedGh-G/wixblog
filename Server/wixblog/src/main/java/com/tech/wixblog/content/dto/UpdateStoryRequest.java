package com.tech.wixblog.content.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;
import java.util.Set;

public record UpdateStoryRequest(

    @Size(
        max = 150,
        message = "Title cannot exceed 150 characters"
    )
    String title,

    @Size(
        max = 300,
        message = "Subtitle cannot exceed 300 characters"
    )
    String subtitle,

    String content,

    @Size(
        max = 500,
        message = "Cover image URL cannot exceed 500 characters"
    )
    String coverImageUrl,

    UUID categoryId,

    Set<UUID> tagIds

) {}