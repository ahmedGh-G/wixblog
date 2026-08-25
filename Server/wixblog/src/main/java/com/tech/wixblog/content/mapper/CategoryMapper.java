package com.tech.wixblog.content.mapper;

import com.tech.wixblog.content.domain.Category;
import com.tech.wixblog.content.dto.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse (Category category);

    Category toDomain (CategoryResponse categoryResponse);
}