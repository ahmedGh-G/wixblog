package com.tech.wixblog.feed.repository;

import com.tech.wixblog.feed.dto.StoryPersonalizationData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class ForYouRepositoryImpl implements ForYouRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /*
    this method returns a list of stories  relative to the user following the author of the
    stories = true.
    else : returns empty list of stories
    * */
    @Override
    public List<StoryPersonalizationData> getPersonalizationData (
            UUID userId,
            List<UUID> storyIds
                                                                 ) {
        if (storyIds == null || storyIds.isEmpty()) {
            return List.of();
        }
        List<Object[]> rows = entityManager.createQuery("""
                                                                SELECT
                                                                    s.id,
                                                                
                                                                    CASE
                                                                        WHEN EXISTS (
                                                                            SELECT f.id
                                                                            FROM Follow f
                                                                            WHERE f.follower.id = :userId
                                                                              AND f.following.id = s.author.id
                                                                        )
                                                                        THEN true
                                                                        ELSE false
                                                                    END,
                                                                
                                                                    (
                                                                        SELECT COUNT(c.id)
                                                                        FROM Comment c
                                                                        WHERE c.story.id = s.id
                                                                    )
                                                                
                                                                FROM Story s
                                                                WHERE s.id IN :storyIds
                                                                """, Object[].class)
                .setParameter("userId", userId)
                .setParameter("storyIds", storyIds)
                .getResultList();
        return rows.stream()
                .map(row -> new StoryPersonalizationData(
                        (UUID) row[0],
                        (Boolean) row[1],
                        false,
                        false,
                        0L,
                        ((Number) row[2]).longValue()
                ))
                .toList();
    }
}