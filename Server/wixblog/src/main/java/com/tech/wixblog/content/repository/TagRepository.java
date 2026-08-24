package com.tech.wixblog.content.repository;

import com.tech.wixblog.content.domain.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository
        extends JpaRepository<Tag, UUID> {

    Optional<Tag> findBySlug(String slug);

    boolean existsByNameIgnoreCase(String name);

    boolean existsBySlug(String slug);
}