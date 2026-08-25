package com.tech.wixblog.social.repository;

import com.tech.wixblog.social.domain.Comment;
import com.tech.wixblog.social.dto.CommentProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CommentRepository
        extends JpaRepository<Comment, UUID> {
    Page<Comment> findByStoryIdOrderByCreatedAtDesc (
            UUID storyId,
            Pageable pageable
                                                    );

    long countByStoryId (
            UUID storyId
                        );

    boolean existsByIdAndAuthorId (
            UUID commentId,
            UUID authorId
                                  );

    void deleteByIdAndAuthorId (
            UUID commentId,
            UUID authorId
                               );

    @Query("""
                SELECT new com.tech.wixblog.social.dto.CommentProjection(
                    c.id,
                    c.content,
                    new com.tech.wixblog.user.dto.PublicUserResponse(
                                            u.id,
                                            u.username,
                                            p.displayName,
                                            p.bio,
                                            p.avatarUrl
                                        ),
                    c.createdAt,
                    c.updatedAt
                )
                FROM Comment c
                JOIN c.author u
                JOIN UserProfile p
                    ON p.userId = u.id
                WHERE c.story.id = :storyId
            """)
    Page<CommentProjection> findCommentProjections (
            UUID storyId,
            Pageable pageable
                                                   );

    @Query("""
                SELECT new com.tech.wixblog.social.dto.CommentProjection(
                    c.id,
                    c.content,
                    new com.tech.wixblog.user.dto.PublicUserResponse(
                                            u.id,
                                            u.username,
                                            p.displayName,
                                            p.bio,
                                            p.avatarUrl
                                        ),
                    c.createdAt,
                    c.updatedAt
                )
                FROM Comment c
                JOIN c.author u
                JOIN UserProfile p
                    ON p.userId = u.id
                WHERE c.id = :commentId
            """)
    Optional<CommentProjection> findCommentProjectionById (
            @Param("commentId") UUID commentId
                                                          );
}