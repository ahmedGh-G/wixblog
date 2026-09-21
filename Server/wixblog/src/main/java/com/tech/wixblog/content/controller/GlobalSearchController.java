package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.GlobalSearchResponse;
import com.tech.wixblog.content.service.GlobalSearchService;
import com.tech.wixblog.user.dto.PublicUserResponse;
import com.tech.wixblog.user.service.UserSearchService;
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

@Tag(name = "Global Search engine",
     description = "Endpoints for system global search engine")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class GlobalSearchController {
    private final GlobalSearchService globalSearchService;
    private final UserSearchService userSearchService;

    @GetMapping
    public ResponseEntity<GlobalSearchResponse> globalSearch (
            @RequestParam String q
                                                             ) {
        return ResponseEntity.ok(
                globalSearchService.search(q)
                                );
    }

    @GetMapping("/users")
    public ResponseEntity<Page<PublicUserResponse>> searchUsers (
            @RequestParam
            String q,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
                                                                ) {
        return ResponseEntity.ok(
                userSearchService.search(
                        q,
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                                        )
                                );
    }

}