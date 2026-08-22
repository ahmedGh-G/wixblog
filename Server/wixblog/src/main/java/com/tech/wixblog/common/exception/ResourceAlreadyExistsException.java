package com.tech.wixblog.common.exception;

public class ResourceAlreadyExistsException
        extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}