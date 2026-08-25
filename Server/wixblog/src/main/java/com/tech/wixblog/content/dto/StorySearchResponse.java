package com.tech.wixblog.content.dto;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.dto.CategoryResponse;
import com.tech.wixblog.content.dto.TagResponse;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record StorySearchResponse(

    UUID id,

    String title,

    String subtitle,

    String coverImageUrl,

    UUID authorId,

    String authorUsername,

    CategoryResponse category,

    Set<TagResponse> tags,

    Instant publishedAt,

    Integer readingTimeMinutes

) {

    public static StorySearchResponse from(
        Story story
    ) {

        return new StorySearchResponse(
            story.getId(),
            story.getTitle(),
            story.getSubtitle(),
            story.getCoverImageUrl(),
            story.getAuthor().getId(),
            story.getAuthor().getUsername(),
            story.getCategory() == null
                ? null
                : CategoryResponse.from(
                    story.getCategory()
                ),
            story.getTags()
                .stream()
                .map(TagResponse::from)
                .collect(Collectors.toSet()),
            story.getPublishedAt(),
            story.getReadingTimeMinutes()
        );
    }
}