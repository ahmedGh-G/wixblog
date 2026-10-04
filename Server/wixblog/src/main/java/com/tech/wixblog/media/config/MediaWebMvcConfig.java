package com.tech.wixblog.media.config;

import com.tech.wixblog.media.service.LocalFilesystemMediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

/**
 * Serves locally stored images over HTTP.
 * <p>
 * Serving is delegated to Spring's static resource handler rather than a bespoke
 * controller: it already handles conditional requests, {@code Last-Modified},
 * {@code Range}, content-type resolution and rejection of {@code ..} in the request
 * path, all of which would otherwise be easy to get subtly wrong.
 * <p>
 * URLs are built to include {@code server.servlet.context-path}, so an image served
 * here is reachable at {@code /api/v1/media/...} by default.
 */
@Configuration
@RequiredArgsConstructor
public class MediaWebMvcConfig
        implements WebMvcConfigurer {

    private final MediaProperties mediaProperties;
    private final LocalFilesystemMediaStorage localStorage;

    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registry) {
        String location = localStorage.getRoot().toUri().toString();
        Duration ttl = mediaProperties.cacheTtl();

        registry.addResourceHandler(mediaProperties.publicPath() + "/**")
                .addResourceLocations(location)
                .setCacheControl(CacheControl.maxAge(ttl)
                                         .cachePublic()
                                         .immutable()
                                );
    }
}