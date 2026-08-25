package com.tech.wixblog.content.mapper;

import com.tech.wixblog.social.domain.Comment;
import com.tech.wixblog.social.dto.CommentResponse;
import com.tech.wixblog.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = UserMapper.class
)
public interface CommentMapper {
    @Mapping(
            target = "author",
            source = "author"
    )
    CommentResponse toResponse (Comment comment);
}