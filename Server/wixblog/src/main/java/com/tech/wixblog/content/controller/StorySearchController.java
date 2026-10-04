package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.service.StorySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Story search",
        description = "Full-text and faceted search across published stories."
                )
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class StorySearchController {

    private final StorySearchService searchService;

    @Operation(
            summary = "Search published stories",
            description = """
                    Matches on title, subtitle and body content, and can be narrowed by category
                    or tag. All filters are optional and combine.

                    `sort` accepts `latest` (the default) or `oldest`; any other value is
                    rejected as a bad request.
                    """
    )
    @GetMapping("/stories")
    public ResponseEntity<Page<StorySearchResponse>> searchStories (
            @Parameter(description = "Search keyword")
            @RequestParam(required = false) String q,
            @Parameter(description = "Restrict results to one category")
            @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Restrict results to one tag slug")
            @RequestParam(required = false) String tag,
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable,
            @Parameter(description = "Sort order: latest or oldest")
            @RequestParam(required = false) String sort
                                             ) {
        StorySearchRequest request =
                new StorySearchRequest(
                        q,
                        categoryId,
                        tag,
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        sort
                                );
        return ResponseEntity.ok(searchService.search(request));
    }
}