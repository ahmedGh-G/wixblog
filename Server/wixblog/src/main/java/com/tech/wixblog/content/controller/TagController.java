package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.content.service.StorySearchService;
import com.tech.wixblog.content.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Tags",
        description = "Endpoints for browsing story tags and the stories filed under them."
                )
@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final StorySearchService storySearchService;

    @Operation(
            summary = "List or search tags",
            description = "Returns every tag, or only those matching `query`. Public."
    )
    @GetMapping
    public ResponseEntity<List<TagResponse>> getTags (
            @Parameter(description = "Optional substring to filter tags by")
            @RequestParam(required = false) String query
                                               ) {
        if (query == null) {
            return ResponseEntity.ok(tagService.getAll());
        }
        return ResponseEntity.ok(tagService.search(query));
    }

    @Operation(
            summary = "List stories carrying a tag",
            description = "Paginated published stories filed under the given tag. Public."
    )
    @GetMapping("/{slug}/stories")
    public ResponseEntity<Page<StorySearchResponse>> getTagStories (
            @Parameter(description = "URL-safe tag identifier, for example 'java'")
            @PathVariable String slug,
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
                                                   ) {
        /*
         * Typed with var rather than naming the type: the Swagger Tag annotation and the
         * domain entity are both called Tag, and importing both is a compile error. This
         * is the one place in the controller layer that needs the entity at all, and only
         * to read its slug, so avoiding the name keeps the annotation readable.
         */
        var tag = tagService.getEntityBySlug(slug);
        StorySearchRequest request =
                new StorySearchRequest(
                        null,
                        null,
                        tag.getSlug(),
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "latest"
                                );
        return ResponseEntity.ok(storySearchService.search(request));
    }
}