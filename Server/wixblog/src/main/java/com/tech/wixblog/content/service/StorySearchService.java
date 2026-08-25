package com.tech.wixblog.content.service;

import com.tech.wixblog.content.domain.StorySearchSort;
import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.dto.StoryResponse;
import com.tech.wixblog.content.repository.StoryRepository;
import com.tech.wixblog.content.repository.StorySearchSpecification;
import com.tech.wixblog.content.dto.StorySearchRequest;
import com.tech.wixblog.content.dto.StorySearchResponse;
import com.tech.wixblog.content.repository.StorySearchSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StorySearchService {

    private final StoryRepository storyRepository;



    public Page<StorySearchResponse> search(
        StorySearchRequest request
    ) {

        Specification<Story> specification =
            StorySearchSpecification.published();

        Specification<Story> text =
            StorySearchSpecification.text(
                request.q()
            );

        if (text != null) {
            specification =
                specification.and(text);
        }

        Specification<Story> category =
            StorySearchSpecification.category(
                request.categoryId()
            );

        if (category != null) {
            specification =
                specification.and(category);
        }

        Specification<Story> tag =
            StorySearchSpecification.tag(
                request.tag()
            );

        if (tag != null) {
            specification =
                specification.and(tag);
        }


        Sort sort =
                switch (
                        StorySearchSort.from(request.sort())
                        ) {

                    case LATEST ->
                            Sort.by(
                                    Sort.Direction.DESC,
                                    "publishedAt"
                                   );

                    case OLDEST ->
                            Sort.by(
                                    Sort.Direction.ASC,
                                    "publishedAt"
                                   );
                };
        PageRequest pageable =
            PageRequest.of(
                request.page(),
                request.size(),
                sort
            );

        return storyRepository
            .findAll(
                specification,
                pageable
            )
            .map(
                StorySearchResponse::from
            );
    }

    public List<StorySearchResponse> searchForGlobalSearch(
            String query,
            int limit
                                                          ) {

        StorySearchRequest request =
                new StorySearchRequest(
                        query,
                        null,
                        null,
                        0,
                        limit,
                        "latest"
                );

        return search(request).getContent();
    }
}