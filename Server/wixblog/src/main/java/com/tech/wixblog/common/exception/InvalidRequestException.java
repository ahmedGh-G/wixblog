package com.tech.wixblog.common.exception;

/**
 * Raised when a request is structurally acceptable but semantically unusable, in a way
 * that should be reported as {@code 400 Bad Request} rather than as a business-rule
 * violation ({@code 422}) or an unsupported media type ({@code 415}).
 * <p>
 * Typical case: an uploaded part that is present but contains no bytes. The request is
 * well-formed and the endpoint does accept file uploads, so neither 415 nor 422 is
 * accurate.
 */
public class InvalidRequestException
        extends RuntimeException {

    public InvalidRequestException (
            String message
                                ) {
        super(message);
    }
}