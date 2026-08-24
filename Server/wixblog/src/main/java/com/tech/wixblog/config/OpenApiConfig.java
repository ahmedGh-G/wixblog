package com.tech.wixblog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wixblogOpenAPI() {

        return new OpenAPI()

                .info(new Info()
                        .title("Wixblog API")
                        .description(
                                """
                                REST API for the Wixblog platform.

                                Wixblog is a Medium-inspired academic
                                blogging and social networking platform.

                                Main modules:
                                - Authentication & Authorization
                                - User Profiles
                                - Social Networking
                                - Story / CMS Management
                                """
                        )
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Wixblog Development Team")
                        )
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(
                                                        SecurityScheme.Type.HTTP
                                                )
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );

    }
}