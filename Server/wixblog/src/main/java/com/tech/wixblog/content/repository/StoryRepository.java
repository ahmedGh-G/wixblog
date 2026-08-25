package com.tech.wixblog.content.repository;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface StoryRepository
        extends JpaRepository<Story, UUID>,
                JpaSpecificationExecutor<Story> {


    Optional<Story> findByIdAndAuthorId(
        UUID storyId,
        UUID authorId
    );

    Page<Story> findByAuthorId(
        UUID authorId,
        Pageable pageable
    );

    Page<Story> findByAuthorIdAndStatus(
        UUID authorId,
        StoryStatus status,
        Pageable pageable
    );

    Page<Story> findByStatus(
        StoryStatus status,
        Pageable pageable
    );

    @Query("""
    SELECT COUNT(s)
    FROM Story s
    WHERE s.category.id = :categoryId
      AND s.status = 'PUBLISHED'
""")
    long countPublishedStories(
            UUID categoryId
                              );
}