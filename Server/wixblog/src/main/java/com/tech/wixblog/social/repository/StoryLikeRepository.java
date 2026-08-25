package com.tech.wixblog.social.repository;

import com.tech.wixblog.social.domain.StoryLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface StoryLikeRepository
        extends JpaRepository<StoryLike, UUID> {
    boolean existsByUserIdAndStoryId (
            UUID userId,
            UUID storyId
                                     );

    long countByStoryId (
            UUID storyId
                        );

    void deleteByUserIdAndStoryId (
            UUID userId,
            UUID storyId
                                  );

    @Query("""
                SELECT COUNT(l)
                FROM StoryLike l
                WHERE l.story.id = :storyId
            """)
    long countLikes (UUID storyId);


}