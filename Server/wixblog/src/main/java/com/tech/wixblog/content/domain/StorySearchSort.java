package com.tech.wixblog.content.domain;

public enum StorySearchSort {

    LATEST,
    OLDEST;

    public static StorySearchSort from(
        String value
    ) {

        if (value == null) {
            return LATEST;
        }

        return switch (
            value.trim().toLowerCase()
        ) {

            case "latest" ->
                LATEST;

            case "oldest" ->
                OLDEST;

            default ->
                throw new IllegalArgumentException(
                    "Unsupported search sort: "
                    + value
                );
        };
    }
}