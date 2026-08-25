package com.tech.wixblog.content.service;

import com.tech.wixblog.content.dto.GlobalSearchResponse;
import com.tech.wixblog.user.service.UserSearchService;
import com.tech.wixblog.user.validator.SearchQueryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GlobalSearchService {
    private static final int GLOBAL_RESULT_LIMIT = 5;
    private final StorySearchService storySearchService;
    private final UserSearchService userSearchService;
    private final TagSearchService tagSearchService;

    public GlobalSearchResponse search (String query) {
        String normalized =
                SearchQueryValidator.normalize(query);
        return new GlobalSearchResponse(
                storySearchService.searchForGlobalSearch(
                        normalized,
                        GLOBAL_RESULT_LIMIT
                                                        ),
                userSearchService.searchForGlobalSearch(
                        normalized,
                        GLOBAL_RESULT_LIMIT
                                                       ),
                tagSearchService.searchForGlobalSearch(
                        normalized,
                        GLOBAL_RESULT_LIMIT
                                                      )
        );
    }
}