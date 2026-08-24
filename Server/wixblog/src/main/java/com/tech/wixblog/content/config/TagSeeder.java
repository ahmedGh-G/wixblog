package com.tech.wixblog.content.config;

import com.tech.wixblog.content.domain.Tag;
import com.tech.wixblog.content.repository.TagRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TagSeeder {

    @Bean
    CommandLineRunner seedTags(
        TagRepository tagRepository
    ) {

        return args -> {

            List<TagData> tags =
                List.of(
                    new TagData("Java", "java"),
                    new TagData(
                        "Spring Boot",
                        "spring-boot"
                    ),
                    new TagData(
                        "Angular",
                        "angular"
                    ),
                    new TagData(
                        "PostgreSQL",
                        "postgresql"
                    ),
                    new TagData(
                        "REST API",
                        "rest-api"
                    ),
                    new TagData(
                        "JWT",
                        "jwt"
                    ),
                    new TagData(
                        "Software Architecture",
                        "software-architecture"
                    ),
                    new TagData(
                        "Database",
                        "database"
                    ),
                    new TagData(
                        "Web Development",
                        "web-development"
                    )
                );

            for (TagData data : tags) {

                if (!tagRepository
                    .existsBySlug(data.slug())) {

                    tagRepository.save(
                        new Tag(
                            data.name(),
                            data.slug()
                        )
                    );
                }
            }
        };
    }

    private record TagData(
        String name,
        String slug
    ) {}
}