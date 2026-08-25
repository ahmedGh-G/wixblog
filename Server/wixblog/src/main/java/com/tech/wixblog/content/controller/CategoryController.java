package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.CategoryResponse;
import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.service.CategoryService;
import com.tech.wixblog.content.service.StorySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/categories")
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
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size
                       ) {
        CategoryResponse category =
                categoryService.getBySlug(slug);
        StorySearchRequest request =
                new StorySearchRequest(
                        null,
                        category.id(),
                        null,
                        page,
                        size,
                        "latest"
                );
        return ResponseEntity.ok(
                storySearchService.search(request)
                                );
    }

}