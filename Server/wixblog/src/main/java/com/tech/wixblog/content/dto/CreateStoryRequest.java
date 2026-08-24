package com.wixblog.content.dto;

import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record CreateStoryRequest(

        @Size(max = 150)
        String title,

        @Size(max = 300)
        String subtitle,

        String content,

        @Size(max = 500)
        String coverImageUrl,

        UUID categoryId,

        Set<UUID> tagIds

) {}