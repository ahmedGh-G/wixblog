package com.tech.wixblog.content.repository;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.feed.dto.StoryRankingData;
import com.tech.wixblog.feed.dto.TrendingCategoryProjection;
import com.tech.wixblog.feed.dto.TrendingTagProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoryRepository extends JpaRepository<Story, UUID>, JpaSpecificationExecutor<Story> {
    Optional<Story> findByIdAndAuthorId (UUID storyId, UUID authorId);

    Page<Story> findByAuthorId (UUID authorId, Pageable pageable);

    Page<Story> findByAuthorIdAndStatus (UUID authorId, StoryStatus status, Pageable pageable);

    Page<Story> findByStatus (StoryStatus status, Pageable pageable);

    @Query("""
                SELECT COUNT(s)
                FROM Story s
                WHERE s.category.id = :categoryId
                  AND s.status = 'PUBLISHED'
            """)
    long countPublishedStories (UUID categoryId);

    @Query("""
                SELECT s
                FROM Story s
                WHERE s.author.id IN (
                    SELECT f.following.id
                    FROM Follow f
                    WHERE f.follower.id = :userId
                )
                AND s.status = :status
                ORDER BY s.publishedAt DESC
            """)
    Page<Story> findFollowingStories (@Param("userId") UUID userId,
                                      @Param("status") StoryStatus status,
                                      Pageable pageable);

    @Query("""
                SELECT s
                FROM Story s
                WHERE s.status = :status
                  AND s.featured = true
                ORDER BY s.featuredAt DESC
            """)
    Page<Story> findFeaturedStories (@Param("status") StoryStatus status, Pageable pageable);

    @Query("""
                SELECT s
                FROM Story s
                WHERE s.status = :status
                ORDER BY s.publishedAt DESC
            """)
    Page<Story> findRecentPublishedStories (@Param("status") StoryStatus status, Pageable pageable);

    @Query("""
                SELECT
                    c.id AS categoryId,
                    c.name AS categoryName,
                    c.slug AS categorySlug,
                    (
                        COUNT(DISTINCT sl.id) +
                        COUNT(DISTINCT cm.id)
                    ) AS engagementCount
                FROM Story s
                JOIN s.category c
                LEFT JOIN StoryLike sl
                    ON sl.story = s
                LEFT JOIN Comment cm
                    ON cm.story = s
                WHERE s.status = :status
                GROUP BY c.id, c.name, c.slug
                ORDER BY (COUNT(DISTINCT sl.id) + COUNT(DISTINCT cm.id)) DESC
            """)
    List<TrendingCategoryProjection> findTrendingCategories (@Param("status") StoryStatus status,
                                                             Pageable pageable);

    @Query("""
            SELECT
                t.id AS tagId,
                t.name AS tagName,
                t.slug AS tagSlug,
                (
                    COUNT(DISTINCT sl.id) +
                    COUNT(DISTINCT cm.id)
                ) AS engagementCount
            FROM Story s
            JOIN s.tags t
            LEFT JOIN StoryLike sl
                ON sl.story = s
            LEFT JOIN Comment cm
                ON cm.story = s
            WHERE s.status = :status
            GROUP BY t.id, t.name, t.slug
            ORDER BY (COUNT(DISTINCT sl.id) + COUNT(DISTINCT cm.id)) DESC
            """)
    List<TrendingTagProjection> findTrendingTags (@Param("status") StoryStatus status,
                                                  Pageable pageable);

    @Query("""
                SELECT
                    s.id AS storyId,
                    COUNT(DISTINCT sl.id) AS likes,
                    COUNT(DISTINCT c.id) AS comments
                FROM Story s
                LEFT JOIN StoryLike sl
                    ON sl.story = s
                LEFT JOIN Comment c
                    ON c.story = s
                WHERE s.id IN :storyIds
                GROUP BY s.id
            """)
    List<StoryRankingData> findRankingDataByStoryIds (@Param("storyIds") List<UUID> storyIds);

}