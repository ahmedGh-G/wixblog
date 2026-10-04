package com.tech.wixblog.common.exception;

/**
 * Raised when an uploaded part is not an accepted media type for the target scope,
 * or when its declared content type does not match its actual bytes.
 * Maps to HTTP 415 Unsupported Media Type.
 */
public class UnsupportedMediaException
        extends RuntimeException {

    public UnsupportedMediaException (
            String message
                                ) {
        super(message);
    }
}