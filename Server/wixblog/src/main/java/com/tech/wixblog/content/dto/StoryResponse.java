package com.tech.wixblog.content.dto;

import com.tech.wixblog.content.domain.StoryStatus;

import java.time.Instant;
import java.util.UUID;

public record StoryResponse(

    UUID id,

    UUID authorId,

    String authorUsername,

    String title,

    String subtitle,

    String content,

    String coverImageUrl,

    StoryStatus status,

    Instant createdAt,

    Instant updatedAt,

    Instant publishedAt

) {}