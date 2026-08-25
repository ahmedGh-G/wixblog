package com.tech.wixblog.content.repository;

import com.tech.wixblog.content.domain.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository
        extends JpaRepository<Tag, UUID> {
    Optional<Tag> findBySlug (String slug);

    boolean existsByNameIgnoreCase (String name);

    boolean existsBySlug (String slug);

    List<Tag> findTop20ByNameContainingIgnoreCaseOrderByNameAsc (
            String query
                                                                );

    @Query("""
            SELECT t
            FROM Tag t
            WHERE
                LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
                OR
                LOWER(t.slug) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY t.name ASC
            """)
    List<Tag> searchTags (
            @Param("query") String query,
            Pageable pageable
                         );
}