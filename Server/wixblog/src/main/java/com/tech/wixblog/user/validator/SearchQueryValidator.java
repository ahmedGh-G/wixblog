package com.tech.wixblog.user.validator;

import com.tech.wixblog.common.exception.InvalidRequestException;

/**
 * Validates and normalises a free-text search query.
 * <p>
 * Raises {@link InvalidRequestException} so a malformed query is reported as
 * {@code 400 Bad Request} through the common exception layer, rather than surfacing
 * as an unhandled {@code IllegalArgumentException} and a {@code 500}.
 */
public final class SearchQueryValidator {

    private static final int MIN_QUERY_LENGTH = 2;
    private static final int MAX_QUERY_LENGTH = 100;

    private SearchQueryValidator () {
    }

    public static String normalize (String query) {
        if (query == null) {
            throw new InvalidRequestException(
                    "Search query must not be null"
            );
        }
        String normalized = query.trim();
        if (normalized.length() < MIN_QUERY_LENGTH) {
            throw new InvalidRequestException(
                    "Search query must contain at least "
                            + MIN_QUERY_LENGTH
                            + " characters"
            );
        }
        if (normalized.length() > MAX_QUERY_LENGTH) {
            throw new InvalidRequestException(
                    "Search query must not exceed "
                            + MAX_QUERY_LENGTH
                            + " characters"
            );
        }
        return normalized;
    }
}