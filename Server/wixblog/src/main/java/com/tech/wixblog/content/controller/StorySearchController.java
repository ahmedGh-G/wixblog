package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.service.StorySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
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
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
            String sort) {
        StorySearchRequest request =
                new StorySearchRequest(
                        q,
                        categoryId,
                        tag,
                        page,
                        size,
                        sort
                );
        return ResponseEntity.ok(
                searchService.search(request)
                                );
    }


}