package com.tech.wixblog.content.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.dto.CreateStoryRequest;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.dto.UpdateStoryRequest;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.content.validation.StoryPublicationValidator;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryPublicationValidator storyPublicationValidator;


    public StoryResponse createDraft(
        UUID authorId,
        CreateStoryRequest request
    ) {

        User author =
            userRepository.findById(authorId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found."
                    )
                );

        Story story =
            new Story(author);

        story.updateContent(
            normalize(request.title()),
            normalize(request.subtitle()),
            request.content(),
            normalize(request.coverImageUrl())
        );

        Story saved =
            storyRepository.save(story);

        return toResponse(saved);
    }

    private StoryResponse toResponse(
        Story story
    ) {

        return new StoryResponse(
            story.getId(),
            story.getAuthor().getId(),
            story.getAuthor().getUsername(),
            story.getTitle(),
            story.getSubtitle(),
            story.getContent(),
            story.getCoverImageUrl(),
            story.getStatus(),
            story.getCreatedAt(),
            story.getUpdatedAt(),
            story.getPublishedAt()
        );
    }

    private String normalize(
        String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized =
            value.trim();

        return normalized.isBlank()
            ? null
            : normalized;
    }



    @Transactional
    public StoryResponse updateStory(
            UUID authorId,
            UUID storyId,
            UpdateStoryRequest request
                                    ) {

        Story story =
                storyRepository
                        .findByIdAndAuthorId(
                                storyId,
                                authorId
                                            )
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "Story not found."
                                             )
                                    );

        if (story.getStatus() ==
                StoryStatus.ARCHIVED) {

            throw new BusinessRuleException(
                    "Archived stories cannot be edited."
            );
        }

        story.updateContent(
                normalize(request.title()),
                normalize(request.subtitle()),
                request.content(),
                normalize(request.coverImageUrl())
                           );

        return toResponse(story);
    }

    @Transactional
    public StoryResponse publishStory(
            UUID authorId,
            UUID storyId
                                     ) {

        Story story =
                storyRepository
                        .findByIdAndAuthorId(
                                storyId,
                                authorId
                                            )
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "Story not found."
                                             )
                                    );

        storyPublicationValidator.validate(
                story
                                          );

        story.publish();

        return toResponse(story);
    }


    @Transactional(readOnly = true)
    public StoryResponse getStory(
            UUID storyId,
            UUID viewerId
                                 ) {

        Story story =
                storyRepository.findById(storyId)
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "Story not found."
                                             )
                                    );

        boolean owner =
                viewerId != null &&
                        story.getAuthor()
                                .getId()
                                .equals(viewerId);

        if (story.getStatus() ==
                StoryStatus.DRAFT &&
                !owner) {

            throw new ResourceNotFoundException(
                    "Story not found."
            );
        }

        if (story.getStatus() ==
                StoryStatus.ARCHIVED &&
                !owner) {

            throw new ResourceNotFoundException(
                    "Story not found."
            );
        }

        return toResponse(story);
    }


    @Transactional
    public void archiveStory(
            UUID authorId,
            UUID storyId
                            ) {

        Story story =
                storyRepository
                        .findByIdAndAuthorId(
                                storyId,
                                authorId
                                            )
                        .orElseThrow(() ->
                                             new ResourceNotFoundException(
                                                     "Story not found."
                                             )
                                    );

        story.archive();
    }


    @Transactional(readOnly = true)
    public Page<StoryResponse> getMyStories(
            UUID authorId,
            StoryStatus status,
            Pageable pageable
                                           ) {

        Page<Story> stories;

        if (status == null) {

            stories =
                    storyRepository.findByAuthorId(
                            authorId,
                            pageable
                                                  );

        } else {

            stories =
                    storyRepository
                            .findByAuthorIdAndStatus(
                                    authorId,
                                    status,
                                    pageable
                                                    );
        }

        return stories.map(
                this::toResponse
                          );
    }
}