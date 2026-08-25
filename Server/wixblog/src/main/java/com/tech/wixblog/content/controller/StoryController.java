package com.tech.wixblog.content.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.dto.UpdateStoryRequest;
import com.tech.wixblog.content.service.StoryService;
import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.social.service.StoryLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Stories Engine",
     description = "Endpoints for authoring, updating, publishing, and retrieving stories.")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;
    private final AuthenticationService authenticationService;
    private final StoryLikeService storyLikeService;

    @Operation(
            summary = "Create a new story draft",
            description = "Creates a new story initialized with DRAFT status. The author profile is determined via the active authentication token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Story draft successfully created."),
            @ApiResponse(responseCode = "400",
                         description = "Invalid request payload attributes provided."),
            @ApiResponse(responseCode = "401",
                         description = "Missing or invalid bearer token credentials.")
    })
    @PostMapping
    public ResponseEntity<StoryResponse> createStory (
            Authentication authentication,
            @Valid @RequestBody com.wixblog.content.dto.CreateStoryRequest request
                                                     ) {
        var authorId = AuthenticatedUser.getId(authentication);
        StoryResponse response = storyService.createDraft(authorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update an existing story",
            description = "Updates the metadata, title, or body contents of a target story. Restricts execution safely to the original author."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Story successfully modified and persisted."),
            @ApiResponse(responseCode = "400", description = "Invalid request validation errors."),
            @ApiResponse(responseCode = "403",
                         description = "Access denied. Caller is not the original author."),
            @ApiResponse(responseCode = "404",
                         description = "Target story ID not found in database.")
    })
    @PutMapping("/{storyId}")
    public ResponseEntity<StoryResponse> updateStory (
            Authentication authentication,
            @Parameter(description = "The unique UUID identifier of the story to update")
            @PathVariable UUID storyId,
            @Valid @RequestBody UpdateStoryRequest request
                                                     ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(storyService.updateStory(authorId, storyId, request));
    }

    @Operation(
            summary = "Publish a story draft",
            description = "Transitions a draft story's status to PUBLISHED making it visible to the public feed system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Story status updated to published successfully."),
            @ApiResponse(responseCode = "403",
                         description = "Access denied. Caller is not the original author."),
            @ApiResponse(responseCode = "404", description = "Target story ID not found.")
    })
    @PostMapping("/{storyId}/publish")
    public ResponseEntity<StoryResponse> publishStory (
            Authentication authentication,
            @Parameter(description = "The unique UUID identifier of the story to publish")
            @PathVariable UUID storyId
                                                      ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(storyService.publishStory(authorId, storyId));
    }

    @Operation(
            summary = "Retrieve a single story by ID",
            description = "Fetches a specific story. Supports anonymous readers for published content, while enforcing ownership checks for drafts."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Story details retrieved successfully."),
            @ApiResponse(responseCode = "403",
                         description = "Access denied. Attempted viewing a private draft without authorship permissions."),
            @ApiResponse(responseCode = "404", description = "Target story ID not found.")
    })
    @GetMapping("/{storyId}")
    @SecurityRequirement(name = "")
    public ResponseEntity<StoryResponse> getStory (
            Authentication authentication,
            @Parameter(description = "The unique UUID identifier of the story to retrieve")
            @PathVariable UUID storyId
                                                  ) {
        UUID viewerId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            viewerId = AuthenticatedUser.getId(authentication);
        }
        return ResponseEntity.ok(storyService.getStory(storyId, viewerId));
    }

    @Operation(
            summary = "Archive and delete a story",
            description = "Soft-deletes or archives an active story. The action is securely restricted to the authenticated author."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                         description = "Story removed and archived successfully. No response content returned."),
            @ApiResponse(responseCode = "403",
                         description = "Access denied. Caller is not the original author."),
            @ApiResponse(responseCode = "404", description = "Target story ID not found.")
    })
    @DeleteMapping("/{storyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStory (
            Authentication authentication,
            @Parameter(description = "The unique UUID identifier of the story to delete")
            @PathVariable UUID storyId
                            ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        storyService.archiveStory(authorId, storyId);
    }

    @Operation(
            summary = "Fetch stories belonging to the current user",
            description = "Retrieves a paginated list of all stories authored by the authenticated caller. Can be filtered by status (DRAFT/PUBLISHED)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                         description = "Paginated workspace list fetched successfully."),
            @ApiResponse(responseCode = "401",
                         description = "Missing or invalid bearer token credentials.")
    })
    @GetMapping("/me")
    public ResponseEntity<Page<StoryResponse>> getMyStories (
            Authentication authentication,
            @Parameter(
                    description = "Optional filter to isolate specific story statuses like DRAFT or PUBLISHED")
            @RequestParam(required = false) StoryStatus status,
            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable
                                                            ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(storyService.getMyStories(authorId, status, pageable));
    }

    @Operation(
            summary = "Get published stories",
            description =
                    "Returns a paginated list of published stories."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Stories retrieved successfully"
            )
    })
    @GetMapping
    public ResponseEntity<Page<StoryResponse>>
    getPublishedStories (
            @PageableDefault(
                    size = 20,
                    sort = "publishedAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
                        ) {
        return ResponseEntity.ok(
                storyService.getPublishedStories(
                        pageable
                                                )
                                );
    }

    @PostMapping("/{storyId}/likes")
    public ResponseEntity<Void> likeStory (
            @PathVariable UUID storyId,
            Authentication authentication
                                          ) {
        UUID userId =
                authenticationService
                        .
                        getAuthenticatedUserId(authentication);
        storyLikeService.like(
                userId,
                storyId
                             );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{storyId}/likes")
    public ResponseEntity<Void> unlikeStory (
            @PathVariable UUID storyId,
            Authentication authentication
                                            ) {
        UUID userId =
                authenticationService
                        .getAuthenticatedUserId(authentication);
        storyLikeService.unlike(
                userId,
                storyId
                               );
        return ResponseEntity.noContent().build();
    }
}
