package com.tech.wixblog.feed.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.feed.dto.AuthorRecommendationResponse;
import com.tech.wixblog.feed.dto.CategoryRecommendationResponse;
import com.tech.wixblog.feed.service.I.RecommendationService;
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

@Tag(name = "Simple based Recommendation algorithm",
     description = "Endpoints for similar recommended authors And/Or categories .")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final AuthenticationService authenticationService;

    @GetMapping("/authors")
    public ResponseEntity<List<AuthorRecommendationResponse>> recommendAuthors (
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit
                                                                               ) {
        UUID userId =
                authenticationService.getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(
                recommendationService.recommendAuthors(userId, limit)
                                );
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryRecommendationResponse>> recommendCategories (
            Authentication authentication,
            @RequestParam(defaultValue = "10") int limit
                                                                                    ) {
        UUID userId =
                authenticationService.getAuthenticatedUserId(authentication);
        return ResponseEntity.ok(
                recommendationService.recommendCategories(userId, limit)
                                );
    }
}