package com.tech.wixblog.social.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.social.domain.Follow;
import com.tech.wixblog.social.dto.*;
import com.tech.wixblog.social.repository.FollowRepository;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserProfile;
import com.tech.wixblog.user.repository.UserProfileRepository;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;


    public FollowResponse follow(
            UUID followerId,
            UUID followingId
                                ) {

        if (followerId.equals(followingId)) {
            throw new BusinessRuleException(
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

    public FollowResponse unfollow(
            UUID followerId,
            UUID followingId
                                  ) {

        validateUserExists(followingId);

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
    public FollowStatusResponse getFollowStatus(
            UUID followerId,
            UUID targetUserId
                                               ) {

        validateUserExists(targetUserId);

        boolean following =
                followRepository
                        .existsByFollowerIdAndFollowingId(
                                followerId,
                                targetUserId
                                                         );

        return new FollowStatusResponse(
                following
        );
    }

    @Transactional(readOnly = true)
    public Page<SocialUserResponse> getFollowers(
            UUID userId,
            Pageable pageable
                                                ) {

        validateUserExists(userId);

        return followRepository
                .findByFollowingId(
                        userId,
                        pageable
                                  )
                .map(follow ->
                             toSocialUserResponse(
                                     follow.getFollower()
                                                 )
                    );
    }

    @Transactional(readOnly = true)
    public Page<SocialUserResponse> getFollowing(
            UUID userId,
            Pageable pageable
                                                ) {

        validateUserExists(userId);

        return followRepository
                .findByFollowerId(
                        userId,
                        pageable
                                 )
                .map(follow ->
                             toSocialUserResponse(
                                     follow.getFollowing()
                                                 )
                    );
    }

    @Transactional(readOnly = true)
    public SocialStatsResponse getSocialStats(
            UUID userId
                                             ) {

        validateUserExists(userId);

        long followers =
                followRepository
                        .countByFollowingId(userId);

        long following =
                followRepository
                        .countByFollowerId(userId);

        return new SocialStatsResponse(
                followers,
                following
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

    private void validateUserExists(
            UUID userId
                                   ) {

        if (!userRepository.existsById(userId)) {

            throw new ResourceNotFoundException(
                    "User not found."
            );
        }
    }

    private SocialUserResponse toSocialUserResponse(
            User user
                                                   ) {

        UserProfile profile =
                userProfileRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User profile not found."
                                             )
                                    );

        return new SocialUserResponse(
                user.getId(),
                user.getUsername(),
                profile.getDisplayName(),
                profile.getAvatarUrl()
        );
    }
}