package com.tech.wixblog.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_users_username",
                        columnNames = "username"
                )
        }
)
/**
 * Deliberately {@code @Getter} rather than {@code @Data}: the generated
 * {@code equals}/{@code hashCode}/{@code toString} from {@code @Data} traverse
 * LAZY associations, which fail with {@code LazyInitializationException} under
 * {@code spring.jpa.open-in-view=false}. Equality is bound to the immutable
 * natural key {@link #id} only, for the same reason.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(
            nullable = false,
            length = 255
    )
    private String email;
    @Column(
            nullable = false,
            length = 30
    )
    private String username;
    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private UserStatus status;
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
    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private UserProfile profile;

    public User (String email, String username, String passwordHash, Role role, UserStatus status) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    /**
     * Maintains the inverse side of the {@code profile} association.
     * <p>
     * {@code UserProfile} is the owning side (it holds {@code @MapsId} and the shared
     * primary key), so JPA will not populate {@code User.profile} automatically. Without
     * this, a freshly registered user reports a null profile in memory even though the
     * row was written, which breaks any code that reads it within the same transaction.
     */
    public void attachProfile (UserProfile profile) {
        this.profile = profile;
    }

    @PrePersist
    protected void onCreate () {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate () {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals (Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User user)) {
            return false;
        }
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode () {
        return getClass().hashCode();
    }

    @Override
    public String toString () {
        return "User{id=%s, username=%s}".formatted(id, username);
    }
}