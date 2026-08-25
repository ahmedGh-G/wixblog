package com.tech.wixblog.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    @Column(
            name = "display_name",
            nullable = false,
            length = 50
    )
    private String displayName;
    @Column(name = "bio", length = 5000)
    private String bio;
    @Column(
            name = "avatar_url",
            length = 500
    )
    private String avatarUrl;
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

    public UserProfile (User user, String displayName) {
        this.user = user;
        this.displayName = displayName;
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

    public void update (
            String displayName,
            String bio,
            String avatarUrl
                       ) {
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }


}