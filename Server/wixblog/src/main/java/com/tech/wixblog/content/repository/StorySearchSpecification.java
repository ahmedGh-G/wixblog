package com.tech.wixblog.content.repository;

import com.tech.wixblog.content.domain.Story;
import com.tech.wixblog.content.domain.StoryStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class StorySearchSpecification {
    private StorySearchSpecification () {
    }

    public static Specification<Story> published () {
        return (root, query, cb) ->
                cb.equal(
                        root.get("status"),
                        StoryStatus.PUBLISHED
                        );
    }

    public static Specification<Story> text (
            String searchTerm
                                            ) {
        if (searchTerm == null ||
                searchTerm.isBlank()) {
            return null;
        }
        String pattern =
                "%" +
                        searchTerm.toLowerCase() +
                        "%";
        return (root, query, cb) -> {
            return cb.or(
                    cb.like(
                            cb.lower(
                                    root.get("title")
                                    ),
                            pattern
                           ),
                    cb.like(
                            cb.lower(
                                    root.get("subtitle")
                                    ),
                            pattern
                           ),
                    cb.like(
                            cb.lower(root.get("content").as(String.class)),
                            pattern
                           )
                        );
        };
    }

    public static Specification<Story> category (
            UUID categoryId
                                                ) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, cb) ->
                cb.equal(
                        root
                                .get("category")
                                .get("id"),
                        categoryId
                        );
    }

    public static Specification<Story> tag (
            String tagSlug
                                           ) {
        if (tagSlug == null ||
                tagSlug.isBlank()) {
            return null;
        }
        return (root, query, cb) -> {
            Join<Object, Object> tags =
                    root.join(
                            "tags",
                            JoinType.INNER
                             );
            query.distinct(true);
            return cb.equal(
                    tags.get("slug"),
                    tagSlug
                           );
        };
    }
}