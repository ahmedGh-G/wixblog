package com.tech.wixblog.content.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_slug",
                        columnNames = "slug"
                ),
                @UniqueConstraint(
                        name = "uk_category_name",
                        columnNames = "name"
                )
        }
)
@NoArgsConstructor
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(
            nullable = false,
            length = 80
    )
    private String name;
    @Column(
            nullable = false,
            length = 100
    )
    private String slug;

    public Category (
            String name,
            String slug
                    ) {
        this.name = name;
        this.slug = slug;
    }


}