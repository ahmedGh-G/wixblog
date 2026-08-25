package com.tech.wixblog.user.repository;

import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository
        extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByUserId (UUID userId);


    @Query("""
    SELECT u
    FROM User u
    WHERE
        LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    Page<User> searchPublicProfiles (
            @Param("query") String query,
            Pageable pageable
                                    );
}
