package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.domain.Tag;
import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.content.service.StorySearchService;
import com.tech.wixblog.content.service.TagService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag Manger",
                                        description = "Endpoints for managing Stories Tags")
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final StorySearchService storySearchService;

    @GetMapping
    public ResponseEntity<List<TagResponse>>
    getTags (
            @RequestParam(required = false)
            String query
            ) {
        if (query == null) {
            return ResponseEntity.ok(
                    tagService.getAll()
                                    );
        }
        return ResponseEntity.ok(
                tagService.search(query)
                                );
    }

    @GetMapping("/{slug}/stories")
    public ResponseEntity<Page<StorySearchResponse>>
    getTagStories (
            @PathVariable String slug,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                  ) {
        Tag tag =
                tagService.getEntityBySlug(slug);
        StorySearchRequest request =
                new StorySearchRequest(
                        null,
                        null,
                        tag.getSlug(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "latest"
                );
        return ResponseEntity.ok(
                storySearchService.search(request)
                                );
    }
}