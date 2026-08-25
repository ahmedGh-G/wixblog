package com.tech.wixblog.social.domain;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(
                        name = "idx_comments_story_id",
                        columnList = "story_id"
                ),
                @Index(
                        name = "idx_comments_author_id",
                        columnList = "author_id"
                ),
                @Index(
                        name = "idx_comments_story_created_at",
                        columnList = "story_id, created_at"
                )
        }
)
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "story_id",
            nullable = false
    )
    private Story story;
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "author_id",
            nullable = false
    )
    private User author;
    @Column(
            name = "content",
            nullable = false,
            length = 2000
    )
    private String content;
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

    public Comment (
            Story story,
            User author,
            String content
                   ) {
        this.story = story;
        this.author = author;
        this.content = content;
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

    public void updateContent (String content) {
        this.content = content;
    }
}