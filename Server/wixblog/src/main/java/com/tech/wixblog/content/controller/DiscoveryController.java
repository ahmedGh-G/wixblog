package com.tech.wixblog.content.controller;

import com.tech.wixblog.content.dto.GlobalSearchResponse;
import com.tech.wixblog.content.service.GlobalSearchService;
import com.tech.wixblog.user.dto.PublicUserResponse;
import com.tech.wixblog.user.service.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class DiscoveryController {
    private final GlobalSearchService globalSearchService;
    private final UserSearchService userSearchService;

    @GetMapping
    public ResponseEntity<GlobalSearchResponse> globalSearch (
            @RequestParam String q
                                                             ) {
        return ResponseEntity.ok(
                globalSearchService.search(q)
                                );
    }

    @GetMapping("/users")
    public ResponseEntity<Page<PublicUserResponse>> searchUsers (
            @RequestParam
            String q,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size
                                                                ) {
        return ResponseEntity.ok(
                userSearchService.search(
                        q,
                        page,
                        size
                                        )
                                );
    }

}