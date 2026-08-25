package com.tech.wixblog.social.service;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {
    private final StoryRepository storyRepository;
    private final com.tech.wixblog.content.mapper.StoryMapper storyMapper;

    public Page<StoryResponse> getFollowingFeed (
            UUID userId,
            Pageable pageable
                                                ) {
        Page<Story> stories =
                storyRepository.findFollowingStories(
                        userId,
                        StoryStatus.PUBLISHED,
                        pageable
                                                    );
        return stories.map(storyMapper::toResponse);
    }
}