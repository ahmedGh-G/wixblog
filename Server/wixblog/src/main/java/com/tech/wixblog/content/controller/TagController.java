package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.domain.Tag;
import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.content.service.StorySearchService;
import com.tech.wixblog.content.service.TagService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/tags")
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
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size
                  ) {
        Tag tag =
                tagService.getEntityBySlug(slug);
        StorySearchRequest request =
                new StorySearchRequest(
                        null,
                        null,
                        tag.getSlug(),
                        page,
                        size,
                        "latest"
                );
        return ResponseEntity.ok(
                storySearchService.search(request)
                                );
    }
}