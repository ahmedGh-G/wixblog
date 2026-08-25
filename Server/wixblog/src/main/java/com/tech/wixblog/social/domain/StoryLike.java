package com.tech.wixblog.social.domain;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "story_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_story_like_user_story",
                        columnNames = {
                                "user_id",
                                "story_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_story_likes_story_id",
                        columnList = "story_id"
                ),
                @Index(
                        name = "idx_story_likes_user_id",
                        columnList = "user_id"
                )
        }
)
@Getter
@NoArgsConstructor
public class StoryLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "story_id",
            nullable = false
    )
    private Story story;
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    public StoryLike (
            User user,
            Story story
                     ) {
        this.user = user;
        this.story = story;
    }

    @PrePersist
    protected void onCreate () {
        this.createdAt = Instant.now();
    }
}