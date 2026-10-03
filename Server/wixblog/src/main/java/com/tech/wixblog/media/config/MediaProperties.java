package com.tech.wixblog.media.config;

import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.domain.MediaStorageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * All tunables for the media subsystem live here, under the {@code app.media} prefix,
 * so nothing about uploads is hard-coded in Java.
 */
@Validated
@ConfigurationProperties(prefix = "app.media")
public record MediaProperties(

        @NotNull
        @DefaultValue("LOCAL")
        MediaStorageType storage,

        /**
         * Root directory for {@link MediaStorageType#LOCAL}. Resolved to an absolute
         * path at startup because a relative {@code file:} location resolves against
         * the process working directory, which differs between an IDE run, a
         * {@code java -jar} launch and a service unit.
         */
        @NotNull
        @DefaultValue("./media")
        String localRoot,

        /**
         * Path the local storage is served under, without a trailing slash.
         * Combined with {@code server.servlet.context-path} to form the public URL.
         */
        @NotNull
        @Pattern(regexp = "^/[^/\\s]*$", message = "must start with '/' and contain no further slashes")
        @DefaultValue("/media")
        String publicPath,

        /**
         * Absolute origin used to build public URLs, e.g. {@code https://cdn.example.com}.
         * Leave blank in development to derive it from the incoming request.
         */
        @DefaultValue("")
        String publicBaseUrl,

        /**
         * When true, a client may point a cover or avatar at an arbitrary absolute
         * http(s) URL instead of a key issued by {@code POST /media/images}.
         * Defaults to false so that every served image is one we control: no
         * hotlinking, consistent caching, and no third party tracking the reader.
         */
        @DefaultValue("false")
        boolean allowExternalUrls,

        /**
         * Re-encodes accepted images before persisting them, discarding EXIF metadata
         * (which can carry GPS coordinates from phone cameras) and defeating files that
         * are simultaneously valid images and valid something else.
         * Animated GIFs are stored untouched so animation survives.
         */
        @DefaultValue("true")
        boolean sanitizeEnabled,

        /**
         * Cache lifetime for served files. Keys are UUIDs and content at a key never
         * changes, so this may safely be very long.
         */
        @DefaultValue("30d")
        Duration cacheTtl,

        /**
         * Per-scope overrides. Any scope omitted here falls back to the defaults
         * declared on {@link MediaScope}.
         * <p>
         * Cascaded validation is declared on the map's value type rather than on the
         * map itself: Hibernate rejects {@code @Valid} on a container and logs
         * HV000271 at startup.
         */
        @DefaultValue
        Map<MediaScope, @Valid ScopeOverrides> scopes,

        @Valid
        @DefaultValue
        Cleanup cleanup
) {

    /**
     * Partial override for a single scope. {@code null} fields mean "keep the default".
     */
    public record ScopeOverrides(
            DataSize maxSize,
            Set<String> contentTypes
                             ) {}

    /**
     * Sweeping of retired assets. Disabled by default so a first boot never deletes
     * anything unexpected; enable once the deployment is stable.
     */
    public record Cleanup(

            @DefaultValue("false")
            boolean enabled,

            /**
             * How long an asset must have been retired before its file is physically
             * removed. The grace period means a rollback within the window still finds
             * the file on disk.
             */
            @DefaultValue("PT24H")
            Duration gracePeriod,

            /**
             * How long an uploaded-but-never-referenced asset is kept before being
             * treated as an orphan. Covers the window where a user uploads an inline
             * image and then abandons or is still composing the story.
             */
            @DefaultValue("PT24H")
            Duration orphanGracePeriod,

            @DefaultValue("100")
            int batchSize
    ) {
        public Cleanup {
            if (batchSize <= 0) {
                batchSize = 100;
            }
        }
    }
}