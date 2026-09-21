package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.CategoryResponse;
import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.service.CategoryService;
import com.tech.wixblog.content.service.StorySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Category Manger",
     description = "Endpoints for managing Stories categories")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final StorySearchService storySearchService;

    @Operation(
            summary = "Get available story categories",
            description =
                    "Returns all categories available for story classification."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>>
    getCategories () {
        return ResponseEntity.ok(
                categoryService.getAll()
                                );
    }

    @Operation(
            summary = "Search category by slug",
            description =
                    "Returns a category search result by slug"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Category retrieved successfully"
    )
    @GetMapping("/{slug}")
    public ResponseEntity<CategoryResponse>
    getBySlug (
            @PathVariable String slug
              ) {
        return ResponseEntity.ok(
                categoryService.getBySlug(slug)
                                );
    }

    @GetMapping("/{slug}/stories")
    public ResponseEntity<Page<StorySearchResponse>>
    getCategoryStories (
            @PathVariable String slug,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                       ) {
        CategoryResponse category =
                categoryService.getBySlug(slug);
        StorySearchRequest request =
                new StorySearchRequest(
                        null,
                        category.id(),
                        null,
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "latest"
                );
        return ResponseEntity.ok(
                storySearchService.search(request)
                                );
    }

}