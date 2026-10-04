package com.tech.wixblog.content.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.dto.CreateStoryRequest;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.dto.UpdateStoryRequest;
import com.tech.wixblog.content.service.StoryService;
import com.tech.wixblog.security.AuthenticatedUser;
import com.tech.wixblog.social.service.StoryLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Story authoring and retrieval.
 * <p>
 * Controllers here hold no error handling: every failure is raised as one of the common
 * exceptions and translated centrally by {@code GlobalExceptionHandler} into the shared
 * {@code ApiError} payload.
 */
@Tag(
        name = "Stories Manager",
        description = "Endpoints for authoring, updating, publishing, and retrieving stories."
                )
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;
    private final AuthenticationService authenticationService;
    private final StoryLikeService storyLikeService;

    @Operation(
            summary = "Create a new story draft",
            description = """
                    Creates a story initialised with DRAFT status. The author is taken from the
                    bearer token, never from the request body.

                    Upload any cover image through POST /media/images?scope=STORY_COVER first and
                    submit the returned URL as `coverImageUrl`. Omit it, or send null, to create a
                    draft without a cover.
                    """
    )
    @PostMapping
    public ResponseEntity<StoryResponse> createStory (
            Authentication authentication,
            @Valid @RequestBody CreateStoryRequest request
                                                     ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        StoryResponse response = storyService.createDraft(authorId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Update an existing story",
            description = """
                    Replaces the editable fields of a story. This is a full replacement rather than
                    a patch, so omitted fields are cleared; send the current values to keep them.

                    Restricted to the original author. Replacing the cover retires the previously
                    stored image.
                    """
    )
    @PutMapping("/{storyId}")
    public ResponseEntity<StoryResponse> updateStory (
            Authentication authentication,
            @Parameter(description = "Unique identifier of the story to update")
            @PathVariable UUID storyId,
            @Valid @RequestBody UpdateStoryRequest request
                                                     ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(storyService.updateStory(authorId, storyId, request));
    }

    @Operation(
            summary = "Publish a story draft",
            description = """
                    Transitions a draft to PUBLISHED so it appears in public feeds. Requires a
                    title, non-blank content and a category.

                    Inline images are re-validated at this point, so an image deleted since the
                    last edit will block publication rather than ship a broken article.
                    """
    )
    @PostMapping("/{storyId}/publish")
    public ResponseEntity<StoryResponse> publishStory (
            Authentication authentication,
            @Parameter(description = "Unique identifier of the story to publish")
            @PathVariable UUID storyId
                                                       ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(storyService.publishStory(authorId, storyId));
    }

    @Operation(
            summary = "Retrieve a single story by ID",
            description = """
                    Public for published stories. Drafts and archived stories are visible only to
                    their author and are reported as not found to everyone else, so their
                    existence is not disclosed.
                    """
    )
    @GetMapping("/{storyId}")
    @SecurityRequirement(name = "")
    public ResponseEntity<StoryResponse> getStory (
            Authentication authentication,
            @Parameter(description = "Unique identifier of the story to retrieve")
            @PathVariable UUID storyId
                                                   ) {
        UUID viewerId = viewerId(authentication);
        return ResponseEntity.ok(storyService.getStory(storyId, viewerId));
    }

    @Operation(
            summary = "Archive a story",
            description = """
                    Soft-deletes a story by moving it to ARCHIVED. Restricted to the author.
                    An archived story can no longer be edited or published, and is hidden from readers.
                    """
    )
    @DeleteMapping("/{storyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveStory (
            Authentication authentication,
            @Parameter(description = "Unique identifier of the story to archive")
            @PathVariable UUID storyId
                            ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        storyService.archiveStory(authorId, storyId);
    }

    @Operation(
            summary = "Fetch stories belonging to the current user",
            description = "Paginated list of stories authored by the caller, optionally filtered by status."
    )
    @GetMapping("/me")
    public ResponseEntity<Page<StoryResponse>> getMyStories (
            Authentication authentication,
            @Parameter(description = "Optional status filter, for example DRAFT or PUBLISHED")
            @RequestParam(required = false) StoryStatus status,
            @ParameterObject
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC)
            Pageable pageable
                                                            ) {
        UUID authorId = AuthenticatedUser.getId(authentication);
        return ResponseEntity.ok(storyService.getMyStories(authorId, status, pageable));
    }

    @Operation(
            summary = "Get published stories",
            description = "Paginated list of published stories, newest first."
    )
    @GetMapping
    public ResponseEntity<Page<StoryResponse>> getPublishedStories (
            @ParameterObject
            @PageableDefault(size = 20, sort = "publishedAt", direction = Sort.Direction.DESC)
            Pageable pageable
                                                              ) {
        return ResponseEntity.ok(storyService.getPublishedStories(pageable));
    }

    @Operation(summary = "Like a story", description = "Adds the caller's like. Idempotent.")
    @PostMapping("/{storyId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeStory (
            @Parameter(description = "Unique identifier of the story to like")
            @PathVariable UUID storyId,
            Authentication authentication
                                                   ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        storyLikeService.like(userId, storyId);
    }

    @Operation(summary = "Remove a like", description = "Removes the caller's like. Idempotent.")
    @DeleteMapping("/{storyId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlikeStory (
            @Parameter(description = "Unique identifier of the story to unlike")
            @PathVariable UUID storyId,
            Authentication authentication
                                                     ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        storyLikeService.unlike(userId, storyId);
    }

    /**
     * Returns the caller's identifier, or {@code null} for an anonymous reader. Public
     * read endpoints accept both, so they must not fail when no principal is present.
     */
    private UUID viewerId (Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return AuthenticatedUser.getId(authentication);
    }
}