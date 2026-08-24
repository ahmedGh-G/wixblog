package com.tech.wixblog.content.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
    name = "tags",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_tag_name",
            columnNames = "name"
        ),
        @UniqueConstraint(
            name = "uk_tag_slug",
            columnNames = "slug"
        )
    }
)
@Data
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(
        nullable = false,
        length = 50
    )
    private String name;

    @Column(
        nullable = false,
        length = 60
    )
    private String slug;


    public Tag(
        String name,
        String slug
    ) {
        this.name = name;
        this.slug = slug;
    }


}