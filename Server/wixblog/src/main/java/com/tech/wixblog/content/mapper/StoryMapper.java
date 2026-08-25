package com.tech.wixblog.content.mapper;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.dto.StorySearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class,
                TagMapper.class
        }
)
public interface StoryMapper {
    @Mapping(
            target = "authorId",
            source = "author.id"
    )
    @Mapping(
            target = "authorUsername",
            source = "author.username"
    )
    StoryResponse toResponse (Story story);

    Story toDomain (StoryResponse storyResponse);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    StorySearchResponse toSearchResponse (Story story);
}