package com.tech.wixblog.social.repository;

import com.tech.wixblog.social.domain.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FollowRepository
        extends JpaRepository<Follow, UUID> {

    boolean existsByFollowerIdAndFollowingId(
            UUID followerId,
            UUID followingId
                                            );

    long countByFollowerId(
            UUID followerId
                          );

    long countByFollowingId(
            UUID followingId
                           );

    void deleteByFollowerIdAndFollowingId(
            UUID followerId,
            UUID followingId
                                         );

    Page<Follow> findByFollowingId(
            UUID followingId,
            Pageable pageable
                                  );

    Page<Follow> findByFollowerId(
            UUID followerId,
            Pageable pageable
                                 );
}