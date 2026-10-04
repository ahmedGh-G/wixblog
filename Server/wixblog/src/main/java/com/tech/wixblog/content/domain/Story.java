package com.tech.wixblog.content.domain;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "stories",
        indexes = {
                @Index(
                        name = "idx_story_author",
                        columnList = "author_id"
                ),
                @Index(
                        name = "idx_story_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_story_published_at",
                        columnList = "published_at"
                )
        }
)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "author_id",
            nullable = false
    )
    private User author;
    @Column(
            name = "title",
            length = 150
    )
    private String title;
    @Column(
            name = "subtitle",
            length = 300
    )
    private String subtitle;
    /**
     * Editor.js block document, serialised as opaque JSON text.
     * <p>
     * Declared as {@code text} rather than {@code @Lob}: Hibernate maps
     * {@code @Lob String} to a Postgres large object ({@code oid}), which cannot be
     * indexed, compared with {@code LIKE} efficiently, or returned by plain selects
     * without extra handling. {@code text} is unlimited length, so the previous
     * {@code length = 65535} ceiling no longer applies here; request-level bounds
     * live on the DTOs via {@code @Size}.
     */
    @Column(
            name = "content",
            columnDefinition = "text"
    )
    private String content;
    @Column(
            name = "cover_image_url",
            length = 1000
    )
    private String coverImageUrl;
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private StoryStatus status;
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;
    @Column(name = "published_at")
    private Instant publishedAt;
    @Column(
            name = "reading_time_minutes"
    )
    private Integer readingTimeMinutes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id"
    )
    private Category category;
    @ManyToMany
    @JoinTable(
            name = "story_tags",
            joinColumns = @JoinColumn(
                    name = "story_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id"
            )
    )
    private Set<Tag> tags = new HashSet<>();
    @Column(
            name = "featured",
            nullable = false
    )
    private boolean featured = false;
    @Column(name = "featured_at")
    private Instant featuredAt;

    public Story (
            User author
                 ) {
        this.author = author;
        this.status = StoryStatus.DRAFT;
    }

    @PrePersist
    protected void onCreate () {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate () {
        this.updatedAt = Instant.now();
    }

    public void updateContent (
            String title,
            String subtitle,
            String content,
            String coverImageUrl
                              ) {
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
    }

    /**
     * Transitions a draft to published.
     *
     * @throws com.tech.wixblog.common.exception.BusinessRuleException when the story is
     *         archived. This uses the same exception as
     *         {@code StoryService.updateStory}'s equivalent guard, so both spellings of
     *         the "archived stories are immutable" rule report an identical 422. It
     *         previously threw {@code IllegalStateException}, which nothing mapped and
     *         therefore surfaced as a 500 for what is only a bad state transition.
     */
    public void publish () {
        if (status == StoryStatus.ARCHIVED) {
            throw new BusinessRuleException(
                    "An archived story cannot be published."
            );
        }
        this.status = StoryStatus.PUBLISHED;
        if (this.publishedAt == null) {
            this.publishedAt = Instant.now();
        }
        this.readingTimeMinutes =
                calculateReadingTime(
                        this.content
                                    );
    }

    private int calculateReadingTime (
            String content
                                     ) {
        if (content == null ||
                content.isBlank()) {
            return 0;
        }
        int wordCount =
                content.trim()
                        .split("\\s+")
                        .length;
        return Math.max(
                1,
                (int) Math.ceil(
                        wordCount / 200.0
                               )
                       );
    }

    public void archive () {
        this.status = StoryStatus.ARCHIVED;
    }

    public Set<Tag> getTags () {
        return Collections.unmodifiableSet(tags);
    }

    public void replaceTags (
            Set<Tag> tags
                            ) {
        this.tags.clear();
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    public void assignCategory (
            Category category
                               ) {
        this.category = category;
    }

    public void feature () {
        this.featured = true;
        this.featuredAt = Instant.now();
    }

    public void unfeature () {
        this.featured = false;
        this.featuredAt = null;
    }
}