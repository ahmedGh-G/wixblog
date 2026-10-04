package com.tech.wixblog.media.repository;

import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaAssetRepository
        extends JpaRepository<MediaAsset, UUID> {

    Optional<MediaAsset> findByStorageKey(String storageKey);

    Optional<MediaAsset> findByPublicUrl(String publicUrl);

    /**
     * Resolves a client-supplied reference to an asset the given user is allowed
     * to attach to their content. Filtering in the query rather than in the service
     * means a mismatched owner can never be loaded into the persistence context at all.
     */
    @Query("""
            SELECT m
            FROM MediaAsset m
            WHERE m.publicUrl = :publicUrl
              AND m.scope = :scope
              AND m.owner.id = :ownerId
              AND m.deletedAt IS NULL
            """)
    Optional<MediaAsset> findOwnedActiveByPublicUrl(
            @Param("publicUrl") String publicUrl,
            @Param("scope") MediaScope scope,
            @Param("ownerId") UUID ownerId
                                   );

    /**
     * Batched lookup used when validating every inline image referenced by a story,
     * to avoid an N+1 query per image in the document.
     */
    @Query("""
            SELECT m
            FROM MediaAsset m
            WHERE m.publicUrl IN :publicUrls
              AND m.owner.id = :ownerId
              AND m.deletedAt IS NULL
            """)
    List<MediaAsset> findOwnedActiveByPublicUrlIn(
            @Param("publicUrls") List<String> publicUrls,
            @Param("ownerId") UUID ownerId
                                          );

    long countByOwnerId(UUID ownerId);

    /**
     * Assets retired before the cutoff, oldest first. Used by the cleanup sweep to
     * retry the physical delete of files whose logical deletion already happened.
     */
    @Query("""
            SELECT m
            FROM MediaAsset m
            WHERE m.deletedAt IS NOT NULL
              AND m.deletedAt < :cutoff
            ORDER BY m.deletedAt ASC
            """)
    List<MediaAsset> findDeletedBefore(
            @Param("cutoff") Instant cutoff,
            Pageable pageable
                                        );

    /**
     * Total bytes currently held by live assets, per scope. Useful for a storage
     * reporting endpoint or an operational dashboard.
     */
    @Query("""
            SELECT m.scope, SUM(m.sizeBytes)
            FROM MediaAsset m
            WHERE m.deletedAt IS NULL
            GROUP BY m.scope
            """)
    List<Object[]> sumLiveSizeBytesByScope();
}