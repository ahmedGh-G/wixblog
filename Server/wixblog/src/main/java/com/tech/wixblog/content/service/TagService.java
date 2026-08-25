package com.tech.wixblog.content.service;

import com.tech.wixblog.common.exception.ResourceNotFoundException;
import com.tech.wixblog.content.domain.Tag;
import com.tech.wixblog.content.dto.TagResponse;
import com.tech.wixblog.content.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository repository;


    public List<TagResponse> getAll () {

        return repository
                .findAll()
                .stream()
                .map(TagResponse::from)
                .toList();
    }

    public List<TagResponse> search(
            String query
                                   ) {

        if (query == null ||
                query.isBlank()) {

            return List.of();
        }

        return repository
                .findTop20ByNameContainingIgnoreCaseOrderByNameAsc(
                        query.trim()
                                                                  )
                .stream()
                .map(TagResponse::from)
                .toList();
    }

    public Tag getEntityBySlug (
            String slug
                               ) {

        return repository
                .findBySlug(slug.toLowerCase())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Tag not found."
                        )
                            );
    }
}