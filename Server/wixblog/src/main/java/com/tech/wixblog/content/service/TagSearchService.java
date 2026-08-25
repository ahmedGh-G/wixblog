package com.tech.wixblog.content.service;

import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.content.mapper.TagMapper;
import com.tech.wixblog.content.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagSearchService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public List<TagResponse> searchForGlobalSearch (
            String query,
            int limit
                                                   ) {
        Pageable pageable = PageRequest.of(0, limit);
        return tagRepository
                .searchTags(query, pageable)
                .stream()
                .map(tagMapper::toResponse)
                .toList();
    }
}