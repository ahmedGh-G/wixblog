package com.tech.wixblog.social.controller;

import com.tech.wixblog.auth.service.AuthenticationService;
import com.tech.wixblog.social.dto.CommentResponse;
import com.tech.wixblog.social.dto.CreateCommentRequest;
import com.tech.wixblog.social.dto.UpdateCommentRequest;
import com.tech.wixblog.social.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommentController {
    private final CommentService commentService;
    private final AuthenticationService authenticationService;

    //todo verify those 401 api responses in swagger | WORKS on PostMan
    @PostMapping("/stories/{storyId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment (
            @PathVariable UUID storyId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication
                                         ) {
        UUID userId =
                authenticationService
                        .getAuthenticatedUserId(authentication);
        return commentService.createComment(
                storyId,
                userId,
                request
                                           );
    }

    @GetMapping(
            "/stories/{storyId}/comments"
    )
    public Page<CommentResponse> getComments (
            @PathVariable UUID storyId,
            @ParameterObject @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
                                             ) {
        return commentService.getComments(
                storyId,
                pageable
                                         );
    }

    @DeleteMapping(
            "/comments/{commentId}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment (
            @PathVariable UUID commentId,
            Authentication authentication
                              ) {
        UUID userId =
                authenticationService
                        .getAuthenticatedUserId(
                                authentication
                                               );
        commentService.deleteComment(
                commentId,
                userId
                                    );
    }

    @PutMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse updateComment (
            @PathVariable UUID commentId,
            @jakarta.validation.Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication
                                         ) {
        UUID userId = authenticationService.getAuthenticatedUserId(authentication);
        return commentService.updateComment(commentId, userId, request);
    }


}