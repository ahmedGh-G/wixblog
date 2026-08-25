package com.tech.wixblog.content.mapper;

import com.tech.wixblog.content.domain.Tag;
import com.tech.wixblog.content.dto.TagResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagResponse toResponse (Tag tag);

    Tag toDomain (TagResponse tagResponse);

}