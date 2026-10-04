package com.tech.wixblog.feed.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.feed.dto.AuthorRecommendationResponse;
import com.tech.wixblog.feed.dto.CategoryRecommendationResponse;
import com.tech.wixblog.feed.service.I.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Recommendations",
        description = "Similar authors and categories, inferred from what the caller already "
                + "follows and reads."
                )
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Recommend authors to follow",
            description = "Authors whose topic profile overlaps the caller's, excluding those "
                    + "already followed."
    )
    @GetMapping("/authors")
    public ResponseEntity<List<AuthorRecommendationResponse>> recommendAuthors (
            Authentication authentication,
            @Parameter(description = "Maximum number of authors to return, 1 to 50")
            @RequestParam(defaultValue = "10") int limit
                                                                       ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(recommendationService.recommendAuthors(userId, limit));
    }

    @Operation(
            summary = "Recommend categories to explore",
            description = "Categories with published stories that the caller is not yet following, "
                    + "ranked by how active those categories are."
    )
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryRecommendationResponse>> recommendCategories (
            Authentication authentication,
            @Parameter(description = "Maximum number of categories to return, 1 to 50")
            @RequestParam(defaultValue = "10") int limit
                                                                                ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(recommendationService.recommendCategories(userId, limit));
    }
}