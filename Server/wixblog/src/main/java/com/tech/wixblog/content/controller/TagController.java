package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.content.repository.TagRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(
        TagRepository tagRepository
    ) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<TagResponse>>
    getTags() {

        List<TagResponse> response =
            tagRepository.findAll(
                            Sort.by("name").ascending()
                )
                .stream()
                .map(TagResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }
}