package com.tech.wixblog.common.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceAlreadyExistsException.class
    )
    public ResponseEntity<ApiError> handleConflict (
            ResourceAlreadyExistsException exception,
            HttpServletRequest request
                                                   ) {

        return buildError(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request,
                List.of()
                         );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
                                                    ) {

        List<ApiError.FieldError> fields =
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

        return buildError(
                HttpStatus.BAD_REQUEST,
                "One or more fields are invalid.",
                request,
                fields
                         );
    }

    @ExceptionHandler(
            BadCredentialsException.class
    )
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException exception,
            HttpServletRequest request
                                                        ) {

        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password.",
                request,
                List.of()
                         );
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<ApiError.FieldError> fields
                                               ) {

        ApiError error =
                new ApiError(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        fields
                );

        return ResponseEntity
                .status(status)
                .body(error);
    }
}