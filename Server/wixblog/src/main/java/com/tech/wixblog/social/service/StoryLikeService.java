package com.tech.wixblog.social.service;

import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.social.domain.StoryLike;
import com.tech.wixblog.social.repository.StoryLikeRepository;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryLikeService {
    private final StoryLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    public void like (
            UUID userId,
            UUID storyId
                     ) {
        if (likeRepository
                .existsByUserIdAndStoryId(
                        userId,
                        storyId
                                         )) {
            return;
        }
        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User not found."
                                )
                                    );
        Story story =
                storyRepository.findById(storyId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Story not found."
                                )
                                    );
        likeRepository.save(
                new StoryLike(user, story)
                           );
    }

    public void unlike (
            UUID userId,
            UUID storyId
                       ) {
        likeRepository.deleteByUserIdAndStoryId(
                userId,
                storyId
                                               );
    }
}