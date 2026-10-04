package com.tech.wixblog.content.domain;

import com.tech.wixblog.common.exception.InvalidRequestException;

/**
 * Sort orders offered by story search.
 */
public enum StorySearchSort {

    LATEST,
    OLDEST;

    /**
     * Parses a caller-supplied sort key.
     * <p>
     * Raises {@link InvalidRequestException} so an unsupported value is reported as
     * {@code 400 Bad Request} through the common exception layer instead of escaping as
     * an unhandled {@code IllegalArgumentException} and a {@code 500}.
     */
    public static StorySearchSort from (
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
                    throw new InvalidRequestException(
                            "Unsupported search sort: "
                                    + value
                    );
        };
    }
}