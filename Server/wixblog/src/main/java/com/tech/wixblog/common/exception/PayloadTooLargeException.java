package com.tech.wixblog.common.exception;

/**
 * Raised when an uploaded part exceeds the size ceiling configured for its scope.
 * Maps to HTTP 413 Content Too Large.
 */
public class PayloadTooLargeException
        extends RuntimeException {

    public PayloadTooLargeException (
            String message
                                ) {
        super(message);
    }
}