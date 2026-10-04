package com.tech.wixblog.media.domain;

import com.tech.wixblog.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Ledger entry for one uploaded image.
 * <p>
 * The owning entity ({@code Story.coverImageUrl}, {@code UserProfile.avatarUrl})
 * still stores a plain URL string, deliberately: those values are already selected
 * by a dozen hand-written JPQL projections and interface projections, and turning
 * them into a relationship would mean rewriting all of them. This table sits beside
 * that denormalised URL and answers the questions a URL alone cannot:
 * <ul>
 *     <li>Does this key still resolve to a live file, or was it superseded?</li>
 *     <li>Does the uploader actually own it? (stops cross-user embedding)</li>
 *     <li>What did it cost us in bytes? (quota and storage reporting)</li>
 * </ul>
 */
@Entity
@Table(
        name = "media_assets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_media_assets_storage_key",
                        columnNames = "storage_key"
                )
        },
        indexes = {
                @Index(
                        name = "idx_media_assets_owner",
                        columnList = "owner_id"
                ),
                @Index(
                        name = "idx_media_assets_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_media_assets_public_url",
                        columnList = "public_url"
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MediaAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Relative path from the storage root, e.g. {@code covers/2026/10/<uuid>.webp}.
     * Generated server-side. Never derived from a client-supplied filename.
     */
    @Column(
            name = "storage_key",
            nullable = false,
            length = 300
    )
    private String storageKey;

    /**
     * Absolute URL handed back to the client and persisted on the owning entity.
     * Denormalised so that rendering a feed never needs a join.
     */
    @Column(
            name = "public_url",
            nullable = false,
            length = 1000
    )
    private String publicUrl;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "scope",
            nullable = false,
            length = 30
    )
    private MediaScope scope;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false
    )
    private User owner;

    /**
     * Kept for display purposes only. Must never participate in path construction.
     */
    @Column(
            name = "original_filename",
            length = 255
    )
    private String originalFilename;

    /**
     * Content type as detected from the file's magic bytes, not as declared by the client.
     */
    @Column(
            name = "content_type",
            nullable = false,
            length = 100
    )
    private String contentType;

    @Column(
            name = "size_bytes",
            nullable = false
    )
    private long sizeBytes;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    /**
     * Set when the asset is superseded or detached. The row is retained rather than
     * deleted so that a reference to a retired key still resolves to a definitive
     * "no longer valid" answer, and so a failed physical delete is recoverable.
     */
    @Column(name = "deleted_at")
    private Instant deletedAt;

    public MediaAsset (
            String storageKey,
            String publicUrl,
            MediaScope scope,
            User owner,
            String originalFilename,
            String contentType,
            long sizeBytes,
            Integer width,
            Integer height
                        ) {
        this.storageKey = storageKey;
        this.publicUrl = publicUrl;
        this.scope = scope;
        this.owner = owner;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.width = width;
        this.height = height;
    }

    @PrePersist
    protected void onCreate () {
        this.createdAt = Instant.now();
    }

    /**
     * Marks this asset as retired. Idempotent: the first call wins, so repeated
     * cleanup passes cannot keep pushing the timestamp forward and starve the
     * physical-delete sweep.
     */
    public void markDeleted () {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
        }
    }

    public boolean isDeleted () {
        return this.deletedAt != null;
    }

    public boolean isOwnedBy (UUID userId) {
        return this.owner != null && this.owner.getId() != null && this.owner.getId().equals(userId);
    }

    @Override
    public String toString () {
        return "MediaAsset{key=%s, scope=%s, deleted=%s}".formatted(storageKey, scope, isDeleted());
    }
}