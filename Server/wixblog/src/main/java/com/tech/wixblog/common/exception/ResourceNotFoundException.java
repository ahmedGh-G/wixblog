package com.tech.wixblog.common.exception;

public class ResourceNotFoundException
        extends RuntimeException {

    public ResourceNotFoundException (String message) {
        super(message);
    }
}