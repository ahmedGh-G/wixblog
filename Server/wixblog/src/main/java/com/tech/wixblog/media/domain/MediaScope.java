package com.tech.wixblog.media.domain;

import org.springframework.util.unit.DataSize;

import java.util.Set;

/**
 * The purpose an uploaded image is being bound to.
 * <p>
 * Each scope owns its storage directory, its size ceiling and its accepted content
 * types. Keeping those three facts on a single enum means a caller can never pass a
 * free-form scope string that is later interpolated into a filesystem path, and the
 * limit for a scope cannot drift away from the directory it guards.
 * <p>
 * Values below are defaults; {@code app.media.scopes} in {@code application.yml}
 * overrides {@link #maxSize} and {@link #acceptedContentTypes} per scope.
 */
public enum MediaScope {

    /**
     * User profile picture. Rendered small and often, so it gets the tightest limit.
     */
    AVATAR(
            "avatars",
            DataSize.ofMegabytes(2),
            Set.of("image/jpeg", "image/png", "image/webp")
    ),

    /**
     * Hero image for a story. Large, shown once, at the top of the article.
     */
    STORY_COVER(
            "covers",
            DataSize.ofMegabytes(5),
            Set.of("image/jpeg", "image/png", "image/webp")
    ),

    /**
     * Image embedded inside a story body. A long article may hold many of these,
     * so the per-file limit is the most generous.
     */
    INLINE_IMAGE(
            "inline",
            DataSize.ofMegabytes(8),
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif")
                            );

    private final String pathSegment;
    private final DataSize defaultMaxSize;
    private final Set<String> defaultAcceptedContentTypes;

    MediaScope (
            String pathSegment,
            DataSize defaultMaxSize,
            Set<String> defaultAcceptedContentTypes
                            ) {
        this.pathSegment = pathSegment;
        this.defaultMaxSize = defaultMaxSize;
        this.defaultAcceptedContentTypes = Set.copyOf(defaultAcceptedContentTypes);
    }

    /**
     * Directory name under the storage root. Must remain a safe single path segment.
     */
    public String getPathSegment () {
        return pathSegment;
    }

    public DataSize getDefaultMaxSize () {
        return defaultMaxSize;
    }

    public Set<String> getDefaultAcceptedContentTypes () {
        return defaultAcceptedContentTypes;
    }
}