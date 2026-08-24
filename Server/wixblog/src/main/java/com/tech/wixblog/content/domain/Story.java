package com.tech.wixblog.content.domain;

import com.tech.wixblog.user.domain.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
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
    }

    public void archive() {

        this.status = StoryStatus.ARCHIVED;
    }
}