package com.tech.wixblog.feed.repository;

import com.tech.wixblog.feed.dto.AuthorRecommendationProjection;
import com.tech.wixblog.feed.dto.CategoryRecommendationProjection;
import com.tech.wixblog.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecommendationRepository extends Repository<User, UUID> {
    /*
     * This method returns a list of AuthorRecommendationProjection as a recommendation for the
     * given userId based on the list of users not followed by the given userId
     * */
    @Query("""
                SELECT
                    u.id AS userId,
                    u.username AS username,
                    p.displayName AS displayName,
                    p.bio AS bio,
                    p.avatarUrl AS avatarUrl
                FROM User u
                JOIN UserProfile p ON p.userId = u.id
                WHERE u.id <> :userId
                  AND u.id NOT IN (
                      SELECT f.following.id
                      FROM Follow f
                      WHERE f.follower.id = :userId
                  )
                ORDER BY u.id
            """)
    List<AuthorRecommendationProjection> findRecommendedAuthors (
            @Param("userId") UUID userId
                                                                );

    /*
     * This method returns a list of CategoryRecommendationProjection where category selection is
     * sort by name from the Published story data
     * */
    @Query("""
                SELECT
                    c.id AS id,
                    c.name AS name,
                    c.slug AS slug
                FROM Category c
                WHERE c.id IN (
                    SELECT s.category.id
                    FROM Story s
                    WHERE s.status = com.tech.wixblog.content.domain.StoryStatus.PUBLISHED
                )
                ORDER BY c.name
            """)
    List<CategoryRecommendationProjection> findRecommendedCategories ();
}