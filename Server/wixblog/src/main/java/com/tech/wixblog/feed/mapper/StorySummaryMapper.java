package com.tech.wixblog.feed.mapper;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.dto.StorySummaryResponse;
import com.tech.wixblog.content.mapper.CategoryMapper;
import com.tech.wixblog.content.mapper.TagMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                CategoryMapper.class,
                TagMapper.class
        }
)
public interface StorySummaryMapper {
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    StorySummaryResponse toResponse (Story story);
}