package com.tech.wixblog.content.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.content.domain.Category;
import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.domain.Tag;
import com.tech.wixblog.content.dto.CreateStoryRequest;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.dto.UpdateStoryRequest;
import com.tech.wixblog.content.mapper.StoryMapper;
import com.tech.wixblog.content.repository.CategoryRepository;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.content.repository.TagRepository;
import com.tech.wixblog.content.validation.InlineImageContentValidator;
import com.tech.wixblog.content.validation.StoryPublicationValidator;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.service.MediaService;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryPublicationValidator storyPublicationValidator;
    private final InlineImageContentValidator inlineImageContentValidator;
    private final MediaService mediaService;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final StoryMapper storyMapper;

    public StoryResponse createDraft (
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
        String coverImageUrl =
                resolveCoverImage(request.coverImageUrl(), authorId);
        // Every inline image in the document must already be uploaded and owned by this
        // author, checked before anything is persisted.
        inlineImageContentValidator.validate(request.content(), author);
        Story story =
                new Story(author);
        story.updateContent(
                normalize(request.title()),
                normalize(request.subtitle()),
                request.content(),
                coverImageUrl
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
        return storyMapper.toResponse(storyRepository.save(story));
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
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found."));
        if (!story.getAuthor().getId().equals(authorId)) {
            throw new BusinessRuleException("Only the author can modify this story.");
        }
        if (story.getStatus() ==
                StoryStatus.ARCHIVED) {
            throw new BusinessRuleException(
                    "Archived stories cannot be edited."
            );
        }
        String previousCover =
                story.getCoverImageUrl();
        String coverImageUrl =
                resolveCoverImage(request.coverImageUrl(), authorId);
        inlineImageContentValidator.validate(request.content(), story.getAuthor());
        story.updateContent(
                normalize(request.title()),
                normalize(request.subtitle()),
                request.content(),
                coverImageUrl
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
        // Only retire the old file once the replacement is accepted, and only when the
        // cover actually changed, so a no-op edit does not churn storage.
        if (!Objects.equals(previousCover, story.getCoverImageUrl())) {
            mediaService.retireReplacedReference(previousCover, authorId);
        }
        return storyMapper.toResponse(story);
    }

    /**
     * Validates a cover reference and confirms the caller owns it.
     * <p>
     * The bean-validation constraint on the DTO has already checked that the URL
     * resolves to a live asset of the right scope. This adds the ownership check, which
     * the constraint cannot perform because it has no access to the authenticated
     * principal.
     */
    private String resolveCoverImage (String requested, UUID authorId) {
        String normalised = normalize(requested);
        mediaService.resolveOwnedReference(normalised, MediaScope.STORY_COVER, authorId);
        return normalised;
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
        // Re-checked at publish time, not only at write time: an image referenced in a
        // draft may have been deleted between the last edit and publication, and a
        // published story must never ship a broken image.
        inlineImageContentValidator.validate(
                story.getContent(),
                story.getAuthor()
                                            );
        story.publish();
        return storyMapper.toResponse(story);
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
        return storyMapper.toResponse(story);
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
            stories = storyRepository
                    .findByAuthorIdAndStatus(
                            authorId,
                            status,
                            pageable
                                            );

        }
        return stories.map(storyMapper::toResponse);
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
                .map(storyMapper::toResponse);
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