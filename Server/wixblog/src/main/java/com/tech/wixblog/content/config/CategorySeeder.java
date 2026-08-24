package com.tech.wixblog.content.config;

import com.tech.wixblog.content.domain.Category;
import com.tech.wixblog.content.repository.CategoryRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CategorySeeder {

    @Bean
    CommandLineRunner seedCategories(
        CategoryRepository categoryRepository
    ) {

        return args -> {

            List<CategoryData> categories =
                List.of(
                    new CategoryData(
                        "Technology",
                        "technology"
                    ),
                    new CategoryData(
                        "Programming",
                        "programming"
                    ),
                    new CategoryData(
                        "Software Engineering",
                        "software-engineering"
                    ),
                    new CategoryData(
                        "Science",
                        "science"
                    ),
                    new CategoryData(
                        "Education",
                        "education"
                    ),
                    new CategoryData(
                        "Business",
                        "business"
                    ),
                    new CategoryData(
                        "Design",
                        "design"
                    ),
                    new CategoryData(
                        "Culture",
                        "culture"
                    ),
                    new CategoryData(
                        "Personal Development",
                        "personal-development"
                    )
                );

            for (CategoryData data : categories) {

                if (!categoryRepository
                    .existsBySlug(data.slug())) {

                    categoryRepository.save(
                        new Category(
                            data.name(),
                            data.slug()
                        )
                    );
                }
            }
        };
    }

    private record CategoryData(
        String name,
        String slug
    ) {}
}