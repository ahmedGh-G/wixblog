package com.tech.wixblog.media.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables the scheduler only when the media cleanup sweep is switched on, so that
 * {@code @EnableScheduling} is not forced on the whole application by an opt-in
 * background job.
 */
@Configuration
@ConditionalOnProperty(
        prefix = "app.media.cleanup",
        name = "enabled",
        havingValue = "true"
                            )
@EnableScheduling
public class MediaSchedulingConfig {
}