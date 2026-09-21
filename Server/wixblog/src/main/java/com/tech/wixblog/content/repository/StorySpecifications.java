package com.tech.wixblog.content.repository;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import com.tech.wixblog.content.domain.Tag;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public final class StorySpecifications {
    private StorySpecifications () {
    }

    public static Specification<Story> published () {
        return (root, query, cb) ->
                cb.equal(
                        root.get("status"),
                        StoryStatus.PUBLISHED
                        );
    }

    public static Specification<Story> hasCategory (String slug) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("category").get("slug"),
                        slug
                        );
    }

    public static Specification<Story> hasTag (String slug) {
        return (root, query, cb) -> {
            Join<Story, Tag> tag = root.join("tags");
            query.distinct(true);
            return cb.equal(
                    tag.get("slug"),
                    slug
                           );
        };
    }

    public static Specification<Story> hasCategoryIn (
            Set<UUID> categoryIds
                                                     ) {
        return (root, query, cb) ->
                root.get("category")
                        .get("id")
                        .in(categoryIds);
    }

    public static Specification<Story> hasTagIn (
            Set<UUID> tagIds
                                                ) {
        return (root, query, cb) -> {
            Join<Story, Tag> tag =
                    root.join("tags");
            query.distinct(true);
            return tag.get("id").in(tagIds);
        };
    }
}