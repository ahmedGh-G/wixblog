package com.tech.wixblog.social.domain;

import com.tech.wixblog.user.domain.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(
    name = "follows",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_follows_follower_following",
            columnNames = {
                "follower_id",
                "following_id"
            }
        )
    }
)
@NoArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "follower_id",
        nullable = false
    )
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "following_id",
        nullable = false
    )
    private User following;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    private Instant createdAt;


    public Follow(
        User follower,
        User following
    ) {
        this.follower = follower;
        this.following = following;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }



}