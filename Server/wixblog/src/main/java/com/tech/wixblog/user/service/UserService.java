package com.tech.wixblog.user.service;

import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserProfile;
import com.tech.wixblog.user.dto.UpdateProfileRequest;
import com.tech.wixblog.user.dto.UserMeResponse;
import com.tech.wixblog.user.repository.UserProfileRepository;
import com.tech.wixblog.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserService(
        UserRepository userRepository,
        UserProfileRepository userProfileRepository
    ) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public UserMeResponse getCurrentUser(
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



    @Transactional
    public UserMeResponse updateProfile(
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

        profile.update(
                request.displayName().trim(),
                normalizeBio(request.bio()),
                normalizeAvatarUrl(request.avatarUrl())
                      );

        return getCurrentUser(userId);
    }

    private String normalizeBio(
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

    private String normalizeAvatarUrl(
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
}