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

@Tag(name = "Story search Api",
     description = "Endpoints for Stories search based")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class StorySearchController {
    private final StorySearchService searchService;

    @Operation(
            summary = "Search published stories",
            description =
                    "Search stories by title, subtitle, content, category or tag."
    )
    @GetMapping("/stories")
    public ResponseEntity<Page<StorySearchResponse>>
    searchStories (
            @Parameter(
                    description = "Search keyword"
            )
            @RequestParam(required = false)
            String q,
            @RequestParam(required = false)
            UUID categoryId,
            @RequestParam(required = false)
            String tag,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable,
            String sort) {
        StorySearchRequest request =
                new StorySearchRequest(
                        q,
                        categoryId,
                        tag,
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        sort
                );
        return ResponseEntity.ok(
                searchService.search(request)
                                );
    }


}