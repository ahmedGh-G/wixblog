package com.tech.wixblog.media.config;

import com.tech.wixblog.media.domain.MediaScope;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Merges the defaults declared on {@link MediaScope} with the optional
 * {@code app.media.scopes.*} overrides, so callers never have to reason about
 * which source a limit came from.
 */
@Component
public class MediaPolicyResolver {

    private final MediaProperties properties;

    public MediaPolicyResolver (MediaProperties properties) {
        this.properties = properties;
    }

    public DataSize maxSizeFor (MediaScope scope) {
        MediaProperties.ScopeOverrides overrides = overridesFor(scope);
        if (overrides != null && overrides.maxSize() != null) {
            return overrides.maxSize();
        }
        return scope.getDefaultMaxSize();
    }

    /**
     * Accepted content types, normalised to lowercase so that comparison against a
     * client's {@code Content-Type} header is case-insensitive.
     */
    public Set<String> acceptedContentTypesFor (MediaScope scope) {
        MediaProperties.ScopeOverrides overrides = overridesFor(scope);
        if (overrides != null && overrides.contentTypes() != null && !overrides.contentTypes().isEmpty()) {
            return overrides.contentTypes()
                    .stream()
                    .map(MediaPolicyResolver::normalise)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return scope.getDefaultAcceptedContentTypes()
                .stream()
                .map(MediaPolicyResolver::normalise)
                .collect(Collectors.toUnmodifiableSet());
    }

    public String describeAcceptedContentTypes (MediaScope scope) {
        return String.join(
                ", ",
                acceptedContentTypesFor(scope)
                        .stream()
                        .sorted()
                        .toList()
                            );
    }

    private MediaProperties.ScopeOverrides overridesFor (MediaScope scope) {
        Map<MediaScope, MediaProperties.ScopeOverrides> configured = properties.scopes();
        return configured == null ? null : configured.get(scope);
    }

    private static String normalise (String contentType) {
        return contentType.trim()
                .toLowerCase(Locale.ROOT);
    }
}