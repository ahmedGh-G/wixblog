package com.tech.wixblog.user.service;

import com.tech.wixblog.user.domain.UserStatus;
import com.tech.wixblog.user.dto.PublicUserResponse;
import com.tech.wixblog.user.repository.UserRepository;
import com.tech.wixblog.user.validator.SearchQueryValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserSearchService {
    private final UserRepository userRepository;

    public UserSearchService (
            UserRepository userRepository
                             ) {
        this.userRepository = userRepository;
    }

    public Page<PublicUserResponse> search (
            String query,
            int page,
            int size
                                           ) {
        String normalized =
                SearchQueryValidator.normalize(query);
        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                              );
        return userRepository.searchPublicUsers(
                normalized,
                UserStatus.ACTIVE,
                pageable
                                               );
    }

    public List<PublicUserResponse> searchForGlobalSearch (
            String query,
            int limit
                                                          ) {
        Pageable pageable =
                PageRequest.of(0, limit);
        return userRepository
                .searchPublicUsers(
                        query,
                        UserStatus.ACTIVE,
                        pageable
                                  )
                .getContent();
    }

}