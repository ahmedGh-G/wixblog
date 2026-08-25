package com.tech.wixblog.content.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.content.domain.Category;
import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.domain.Tag;
import com.tech.wixblog.content.dto.CategoryResponse;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.content.dto.UpdateStoryRequest;
import com.tech.wixblog.content.repository.CategoryRepository;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.content.repository.TagRepository;
import com.tech.wixblog.content.validation.StoryPublicationValidator;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryPublicationValidator storyPublicationValidator;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public StoryResponse createDraft (
            UUID authorId,
            com.wixblog.content.dto.CreateStoryRequest request
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
        Category category =
                resolveCategory(
                        request.categoryId()
                               );
        Set<Tag> tags =
                resolveTags(
                        request.tagIds()
                           );
        story.assignCategory(category);
        story.replaceTags(tags);
        Story saved =
                storyRepository.save(story);
        return toResponse(saved);
    }

    private StoryResponse toResponse (
            Story story
                                     ) {
        CategoryResponse category =
                story.getCategory() == null
                        ? null
                        : CategoryResponse.from(
                        story.getCategory()
                                               );
        Set<TagResponse> tags =
                story.getTags()
                        .stream()
                        .map(TagResponse::from)
                        .collect(Collectors.toSet());
        return new StoryResponse(
                story.getId(),
                story.getAuthor().getId(),
                story.getAuthor().getUsername(),
                story.getTitle(),
                story.getSubtitle(),
                story.getContent(),
                story.getCoverImageUrl(),
                story.getStatus(),
                category,
                tags,
                story.getCreatedAt(),
                story.getUpdatedAt(),
                story.getPublishedAt()
        );
    }

    private String normalize (
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
    public StoryResponse updateStory (
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
        story.assignCategory(
                resolveCategory(
                        request.categoryId()
                               )
                            );
        story.replaceTags(
                resolveTags(
                        request.tagIds()
                           )
                         );
        return toResponse(story);
    }

    @Transactional
    public StoryResponse publishStory (
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
    public StoryResponse getStory (
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
    public void archiveStory (
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
    public Page<StoryResponse> getMyStories (
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

    private Category resolveCategory (
            UUID categoryId
                                     ) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository
                .findById(categoryId)
                .orElseThrow(() ->
                                     new ResourceNotFoundException(
                                             "Category not found."
                                     )
                            );
    }

    private Set<Tag> resolveTags (
            Set<UUID> tagIds
                                 ) {
        if (tagIds == null ||
                tagIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Tag> tags =
                tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new ResourceNotFoundException(
                    "One or more tags were not found."
            );
        }
        return new HashSet<>(tags);
    }

    @Transactional(readOnly = true)
    public Page<StoryResponse> getPublishedStories (
            Pageable pageable
                                                   ) {
        return storyRepository
                .findByStatus(
                        StoryStatus.PUBLISHED,
                        pageable
                             )
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public long countPublishedStories (
            UUID categoryId
                                      ) {
        return storyRepository
                .countPublishedStories(
                        categoryId
                                      );
    }
}