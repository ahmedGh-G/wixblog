package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.GlobalSearchResponse;
import com.tech.wixblog.content.service.GlobalSearchService;
import com.tech.wixblog.user.dto.PublicUserResponse;
import com.tech.wixblog.user.service.UserSearchService;
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

@Tag(
        name = "Search",
        description = "Cross-entity search across stories and public profiles."
                )
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class GlobalSearchController {

    private final GlobalSearchService globalSearchService;
    private final UserSearchService userSearchService;

    @Operation(
            summary = "Search across stories and users",
            description = "Returns grouped results for one query. Public. The query must be at "
                    + "least two characters."
    )
    @GetMapping
    public ResponseEntity<GlobalSearchResponse> globalSearch (
            @Parameter(description = "Search term")
            @RequestParam String q
                                           ) {
        return ResponseEntity.ok(globalSearchService.search(q));
    }

    @Operation(
            summary = "Search public profiles",
            description = "Matches on username and display name. Public. The query must be at "
                    + "least two characters."
    )
    @GetMapping("/users")
    public ResponseEntity<Page<PublicUserResponse>> searchUsers (
            @Parameter(description = "Search term")
            @RequestParam String q,
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
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