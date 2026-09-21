package com.tech.wixblog.feed.repository;

import com.tech.wixblog.feed.dto.StoryPersonalizationData;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ForYouRepository {
    List<StoryPersonalizationData> getPersonalizationData (
            UUID userId,
            List<UUID> storyIds
                                                          );
}