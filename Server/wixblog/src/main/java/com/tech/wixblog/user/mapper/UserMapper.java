package com.tech.wixblog.user.mapper;

import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.dto.PublicUserResponse;
import com.tech.wixblog.user.dto.UserMeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(
            target = "displayName",
            source = "profile.displayName"
    )
    @Mapping(
            target = "bio",
            source = "profile.bio"
    )
    @Mapping(
            target = "avatarUrl",
            source = "profile.avatarUrl"
    )
    PublicUserResponse toPublicResponse (User user);

    @Mapping(
            target = "displayName",
            source = "profile.displayName"
    )
    @Mapping(
            target = "bio",
            source = "profile.bio"
    )
    @Mapping(
            target = "avatarUrl",
            source = "profile.avatarUrl"
    )
    UserMeResponse toMeResponse (User user);
}