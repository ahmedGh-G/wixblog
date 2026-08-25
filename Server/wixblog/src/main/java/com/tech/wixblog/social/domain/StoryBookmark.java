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
        name = "story_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_story_bookmark_user_story",
                        columnNames = {
                                "user_id",
                                "story_id"
                        }
                )
        }
)
@Getter
@NoArgsConstructor
public class StoryBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
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

    public StoryBookmark (
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