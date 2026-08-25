package com.tech.wixblog.social.service;

import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.social.domain.Comment;
import com.tech.wixblog.social.dto.CommentProjection;
import com.tech.wixblog.social.dto.CommentResponse;
import com.tech.wixblog.social.dto.CreateCommentRequest;
import com.tech.wixblog.social.dto.UpdateCommentRequest;
import com.tech.wixblog.social.repository.CommentRepository;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.dto.PublicUserResponse;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    public Page<CommentResponse> getComments (
            UUID storyId,
            Pageable pageable
                                             ) {
        if (!storyRepository.existsById(storyId)) {
            throw new ResourceNotFoundException(
                    "Story not found."
            );
        }
        return commentRepository
                .findCommentProjections(
                        storyId,
                        pageable
                                       )
                .map(this::toResponse);
    }

    @Transactional
    public CommentResponse createComment (
            UUID storyId,
            UUID authenticatedUserId,
            CreateCommentRequest request
                                         ) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() ->
                                     new ResourceNotFoundException(
                                             "Story not found."
                                     )
                            );
        User author = userRepository.findById(
                authenticatedUserId
                                             ).orElseThrow(() ->
                                                                   new ResourceNotFoundException(
                                                                           "User not found."
                                                                   )
                                                          );
        String content = request.content().trim();
        Comment comment = new Comment(
                story,
                author,
                content
        );
        Comment saved =
                commentRepository.save(comment);
        return commentRepository
                .findCommentProjectionById(saved.getId())
                .map(this::toResponse)
                .orElseThrow(() ->
                                     new ResourceNotFoundException(
                                             "Comment could not be loaded."
                                     )
                            );
    }

    @Transactional
    public void deleteComment (
            UUID commentId,
            UUID authenticatedUserId
                              ) {
        Comment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "Comment not found."
                                             )
                                    );
        UUID authorId =
                comment.getAuthor().getId();
        if (!authorId.equals(authenticatedUserId)) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this comment."
            );
        }
        commentRepository.delete(comment);
    }

    private CommentResponse toResponse (
            CommentProjection projection
                                       ) {
        PublicUserResponse author =
                new PublicUserResponse(
                        projection.id(),
                        projection.author().username(),
                        projection.author().displayName(),
                        projection.author().bio(),
                        projection.author().avatarUrl()
                );
        return new CommentResponse(
                projection.id(),
                projection.content(),
                author,
                projection.createdAt(),
                projection.updatedAt()
        );
    }

    @Transactional
    public CommentResponse updateComment (
            UUID commentId,
            UUID authenticatedUserId,
            UpdateCommentRequest request
                                         ) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found."));
        // Guard clause: Enforce strict ownership authorization check
        if (!comment.getAuthor().getId().equals(authenticatedUserId)) {
            throw new AccessDeniedException("You are not allowed to update this comment.");
        }
        // Clean string spaces and mutate state
        comment.updateContent(request.content().trim());
        // Save flushing is handled automatically at transaction commit phase
        return commentRepository.findCommentProjectionById(comment.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Updated comment could not be loaded."));
    }

}