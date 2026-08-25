package com.tech.wixblog.user.validator;

public final class SearchQueryValidator {
    private static final int MIN_QUERY_LENGTH = 2;
    private static final int MAX_QUERY_LENGTH = 100;

    private SearchQueryValidator () {
    }

    public static String normalize (String query) {
        if (query == null) {
            throw new IllegalArgumentException(
                    "Search query must not be null"
            );
        }
        String normalized = query.trim();
        if (normalized.length() < MIN_QUERY_LENGTH) {
            throw new IllegalArgumentException(
                    "Search query must contain at least "
                            + MIN_QUERY_LENGTH
                            + " characters"
            );
        }
        if (normalized.length() > MAX_QUERY_LENGTH) {
            throw new IllegalArgumentException(
                    "Search query must not exceed "
                            + MAX_QUERY_LENGTH
                            + " characters"
            );
        }
        return normalized;
    }
}