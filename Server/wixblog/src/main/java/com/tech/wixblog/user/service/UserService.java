package com.tech.wixblog.user.service;

import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.service.MediaService;
import com.tech.wixblog.social.repository.FollowRepository;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserProfile;
import com.tech.wixblog.user.dto.PublicUserProfileResponse;
import com.tech.wixblog.user.dto.UpdateProfileRequest;
import com.tech.wixblog.user.dto.UserMeResponse;
import com.tech.wixblog.user.repository.UserProfileRepository;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowRepository followRepository;
    private final MediaService mediaService;

    public UserMeResponse getCurrentUser (
            UUID userId
                                         ) {
        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User not found."
                                             )
                                    );
        UserProfile profile =
                userProfileRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User profile not found."
                                             )
                                    );
        return new UserMeResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getAvatarUrl(),
                user.getRole(),
                user.getStatus()
        );
    }


   /* public UserMeResponse getCurrentUser (
            UUID userId
                                         ) {
        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User not found."
                                             )
                                    );
        UserProfile profile =
                userProfileRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User profile not found."
                                             )
                                    );
        return userMapper.toMeResponse(user);
    }*/

    @Transactional
    public UserMeResponse updateProfile (
            UUID userId,
            UpdateProfileRequest request
                                        ) {
        UserProfile profile =
                userProfileRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User profile not found."
                                             )
                                    );
        String previousAvatar =
                profile.getAvatarUrl();
        String avatarUrl =
                resolveAvatarUrl(request.avatarUrl(), userId);

        profile.update(
                request.displayName().trim(),
                normalizeBio(request.bio()),
                avatarUrl
                      );

        // Only retire the old file once the replacement has been accepted, and only when
        // the avatar actually changed, so a no-op save does not churn storage.
        if (!Objects.equals(previousAvatar, avatarUrl)) {
            mediaService.retireReplacedReference(previousAvatar, userId);
        }

        return getCurrentUser(userId);
    }

    /**
     * Validates an avatar reference and confirms the caller owns it.
     * <p>
     * The bean-validation constraint on {@link UpdateProfileRequest} has already checked
     * that the URL resolves to a live asset of scope {@code AVATAR}. This adds the
     * ownership check, which the constraint cannot perform because it has no access to
     * the authenticated principal.
     */
    private String resolveAvatarUrl (String requested, UUID userId) {
        String normalised = normalizeAvatarUrl(requested);
        mediaService.resolveOwnedReference(normalised, MediaScope.AVATAR, userId);
        return normalised;
    }

    private String normalizeBio (
            String bio
                                ) {
        if (bio == null) {
            return null;
        }
        String normalized =
                bio.trim();
        return normalized.isBlank()
                ? null
                : normalized;
    }

    private String normalizeAvatarUrl (
            String avatarUrl
                                      ) {
        if (avatarUrl == null) {
            return null;
        }
        String normalized =
                avatarUrl.trim();
        return normalized.isBlank()
                ? null
                : normalized;
    }

    @Transactional(readOnly = true)
    public PublicUserProfileResponse getPublicProfile (
            String username,
            UUID viewerId
                                                      ) {
        User user =
                userRepository
                        .findByUsernameIgnoreCase(username)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User not found."
                                             )
                                    );
        UserProfile profile =
                userProfileRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "User profile not found."
                                             )
                                    );
        long followersCount =
                followRepository
                        .countByFollowingId(user.getId());
        long followingCount =
                followRepository
                        .countByFollowerId(user.getId());
        boolean following =
                viewerId != null &&
                        followRepository
                                .existsByFollowerIdAndFollowingId(
                                        viewerId,
                                        user.getId()
                                                                 );
        return new PublicUserProfileResponse(
                user.getId(),
                user.getUsername(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getAvatarUrl(),
                followersCount,
                followingCount,
                following
        );
    }


}