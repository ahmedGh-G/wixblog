package com.tech.wixblog.content.domain;

import com.tech.wixblog.user.domain.User;

import jakarta.persistence.*;
import lombok.Data;
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
@Data
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

    @Lob
    @Column(
        name = "content",
        columnDefinition = "TEXT"
    )
    private String content;

    @Column(
        name = "cover_image_url",
        length = 500
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

    public Story(
        User author
    ) {
        this.author = author;
        this.status = StoryStatus.DRAFT;
    }

    @PrePersist
    protected void onCreate() {

        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = Instant.now();
    }

    public void updateContent(
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

    public void publish() {

        if (status == StoryStatus.ARCHIVED) {
            throw new IllegalStateException(
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


    private int calculateReadingTime(
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

    public void archive() {

        this.status = StoryStatus.ARCHIVED;
    }


    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }
    public void replaceTags(
            Set<Tag> tags
                           ) {

        this.tags.clear();

        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    public void assignCategory(
            Category category
                              ) {
        this.category = category;
    }
}