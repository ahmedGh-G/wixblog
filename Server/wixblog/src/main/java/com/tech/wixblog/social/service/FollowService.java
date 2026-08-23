package com.tech.wixblog.social.service;

import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.social.domain.Follow;
import com.tech.wixblog.social.dto.FollowResponse;
import com.tech.wixblog.social.repository.FollowRepository;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(
        FollowRepository followRepository,
        UserRepository userRepository
    ) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FollowResponse follow(
        UUID followerId,
        UUID followingId
    ) {

        if (followerId.equals(followingId)) {

            throw new IllegalArgumentException(
                "You cannot follow yourself."
            );
        }

        User follower =
            getUser(followerId);

        User following =
            getUser(followingId);

        if (!followRepository
            .existsByFollowerIdAndFollowingId(
                followerId,
                followingId
            )) {

            followRepository.save(
                new Follow(
                    follower,
                    following
                )
            );
        }

        return new FollowResponse(
            followerId,
            followingId,
            true
        );
    }

    @Transactional
    public FollowResponse unfollow(
        UUID followerId,
        UUID followingId
    ) {

        followRepository
            .deleteByFollowerIdAndFollowingId(
                followerId,
                followingId
            );

        return new FollowResponse(
            followerId,
            followingId,
            false
        );
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(
        UUID followerId,
        UUID followingId
    ) {

        return followRepository
            .existsByFollowerIdAndFollowingId(
                followerId,
                followingId
            );
    }

    private User getUser(UUID userId) {

        return userRepository
            .findById(userId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "User not found."
                )
            );
    }
}