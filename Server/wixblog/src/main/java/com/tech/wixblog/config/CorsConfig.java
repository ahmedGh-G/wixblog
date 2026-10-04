package com.tech.wixblog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Registers the CORS policy consumed by {@code SecurityConfig#cors}.
 * <p>
 * Credentials are deliberately not permitted: this API is stateless and
 * token-authenticated, and allowing credentials would force the
 * {@code Access-Control-Allow-Origin} header to name a single origin, which is a
 * common source of production outages.
 * <p>
 * {@code Location}, {@code ETag} and {@code Last-Modified} are exposed so that the
 * browser can read them from an upload response and issue conditional requests for
 * served images.
 */
@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource () {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.allowedOrigins());
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
                                                   ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(
                HttpHeaders.LOCATION,
                HttpHeaders.CONTENT_DISPOSITION,
                HttpHeaders.ETAG,
                HttpHeaders.LAST_MODIFIED
                                           ));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(corsProperties.maxAge().toSeconds());

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}