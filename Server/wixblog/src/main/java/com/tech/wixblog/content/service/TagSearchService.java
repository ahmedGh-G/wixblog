package com.tech.wixblog.content.service;

import com.tech.wixblog.content.dto.TagSearchResponse;
import com.tech.wixblog.content.repository.TagRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TagSearchService {
    private final TagRepository tagRepository;

    public TagSearchService (
            TagRepository tagRepository
                            ) {
        this.tagRepository = tagRepository;
    }

    public List<TagSearchResponse> searchForGlobalSearch (
            String query,
            int limit
                                                         ) {
        Pageable pageable = PageRequest.of(0, limit);
        return tagRepository
                .searchTags(query, pageable)
                .stream()
                .map(TagSearchResponse::from)
                .toList();
    }
}