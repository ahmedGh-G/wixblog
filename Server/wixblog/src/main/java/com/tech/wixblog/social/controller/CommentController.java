package com.tech.wixblog.social.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.social.dto.CommentResponse;
import com.tech.wixblog.social.dto.CreateCommentRequest;
import com.tech.wixblog.social.dto.UpdateCommentRequest;
import com.tech.wixblog.social.service.CommentService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(
        name = "Comments",
        description = "Reader comments on stories. Deletion and editing are restricted to "
                + "the comment's author."
                )
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Comment on a story",
            description = "Attributes the comment to the caller; the author is taken from the token."
    )
    @PostMapping("/stories/{storyId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment (
            @Parameter(description = "Story being commented on")
            @PathVariable UUID storyId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
                                         ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        return commentService.createComment(storyId, userId, request);
    }

    @Operation(
            summary = "List a story's comments",
            description = "Oldest of the newest first. Public."
    )
    @SecurityRequirement(name = "")
    @GetMapping("/stories/{storyId}/comments")
    public Page<CommentResponse> getComments (
            @Parameter(description = "Story whose comments to list")
            @PathVariable UUID storyId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
                                         ) {
        return commentService.getComments(storyId, pageable);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Restricted to the comment's author."
    )
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment (
            @Parameter(description = "Comment to delete")
            @PathVariable UUID commentId,
            Authentication authentication
                             ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        commentService.deleteComment(commentId, userId);
    }

    @Operation(
            summary = "Edit a comment",
            description = "Replaces the comment body. Restricted to the comment's author."
    )
    @PutMapping("/comments/{commentId}")
    public CommentResponse updateComment (
            @Parameter(description = "Comment to edit")
            @PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication
                                         ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        return commentService.updateComment(commentId, userId, request);
    }
}