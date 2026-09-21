package com.tech.wixblog.feed.service.strategy;

import com.tech.wixblog.feed.dto.StoryPersonalizationData;
import com.tech.wixblog.feed.repository.ForYouRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForYouPersonalizationService {
    private final ForYouRepository forYouRepository;

    public Map<UUID, StoryPersonalizationData> getPersonalizationData (
            UUID userId,
            List<UUID> storyIds
                                                                      ) {
        if (storyIds == null || storyIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return forYouRepository
                .getPersonalizationData(userId, storyIds)
                .stream()
                .collect(Collectors.toMap(
                        StoryPersonalizationData::storyId,
                        Function.identity()
                                         ));
    }
}