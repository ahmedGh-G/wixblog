package com.tech.wixblog.user.repository;

import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserStatus;
import com.tech.wixblog.user.dto.PublicUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase (String email);

    boolean existsByUsernameIgnoreCase (String username);

    Optional<User> findByEmailIgnoreCase (String email);

    Optional<User> findByUsernameIgnoreCase (String username);

    @Query("""
            SELECT new com.tech.wixblog.user.dto.PublicUserResponse(
                u.id,
                u.username,
                p.displayName,
                p.bio,
                p.avatarUrl
            )
            FROM User u
            JOIN UserProfile p ON p.user = u
            WHERE u.status = :status
              AND (
                  LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
                  OR
                  LOWER(p.displayName) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            ORDER BY u.username ASC
            """)
    Page<PublicUserResponse> searchPublicUsers (
            @Param("query") String query,
            @Param("status") UserStatus status,
            Pageable pageable
                                               );
}