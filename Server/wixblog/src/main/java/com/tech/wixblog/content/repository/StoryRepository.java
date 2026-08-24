package com.tech.wixblog.content.repository;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoryRepository
        extends JpaRepository<Story, UUID> {

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
}