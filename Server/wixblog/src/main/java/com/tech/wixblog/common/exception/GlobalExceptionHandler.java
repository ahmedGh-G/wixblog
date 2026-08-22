package com.tech.wixblog.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleConflict(
        ResourceAlreadyExistsException exception,
        HttpServletRequest request
    ) {

        ApiError error = new ApiError(
            Instant.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            exception.getMessage(),
            request.getRequestURI(),
            List.of()
        );

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {

        List<ApiError.FieldError> fieldErrors =
            exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                    new ApiError.FieldError(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                )
                .toList();

        ApiError error = new ApiError(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            "One or more fields are invalid.",
            request.getRequestURI(),
            fieldErrors
        );

        return ResponseEntity
            .badRequest()
            .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
        Exception exception,
        HttpServletRequest request
    ) {

        ApiError error = new ApiError(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred.",
            request.getRequestURI(),
            List.of()
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }
}