package com.tech.wixblog.content.service;

import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.content.dto.CategoryResponse;
import com.tech.wixblog.content.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService (
            CategoryRepository repository
                           ) {
        this.repository = repository;
    }

    public List<CategoryResponse> getAll () {
        return repository
                .findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getBySlug (
            String slug
                                      ) {
        return repository
                .findBySlug(
                        slug.toLowerCase()
                           )
                .map(CategoryResponse::from)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Category not found."
                        )
                            );
    }


}