package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.CategoryResponse;
import com.tech.wixblog.content.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;




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
    getCategories() {

        List<CategoryResponse> response =
            categoryRepository.findAll(
                            Sort.by("name").ascending()
                )
                .stream()
                .map(CategoryResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}