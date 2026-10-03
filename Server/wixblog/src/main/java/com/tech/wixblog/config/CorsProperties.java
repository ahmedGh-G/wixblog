package com.tech.wixblog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;

/**
 * Cross-origin settings for the Angular client.
 * <p>
 * Without these the browser refuses every call from the dev server on
 * {@code http://localhost:4200} before it reaches Spring Security, which makes the
 * upload endpoints impossible to exercise locally.
 */
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(

        /**
         * Origins permitted to call this API. Listed explicitly rather than using a
         * {@code *} wildcard so that a deployment cannot be opened up by accident.
         */
        @DefaultValue({"http://localhost:4200", "http://127.0.0.1:4200"})
        List<String> allowedOrigins,

        @DefaultValue("3600s")
        Duration maxAge
) {

    public List<String> allowedOrigins () {
        return allowedOrigins == null || allowedOrigins.isEmpty()
                ? List.of()
                : List.copyOf(allowedOrigins);
    }
}