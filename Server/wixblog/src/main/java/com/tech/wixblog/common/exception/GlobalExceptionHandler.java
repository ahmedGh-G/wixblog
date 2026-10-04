package com.tech.wixblog.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Single translation point from exceptions to {@link ApiError} payloads.
 * Every handler returns the same response shape so clients never have to branch
 * on response format, only on status code.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* ------------------------------------------------------------------ */
    /* Conflict / existence                                                */
    /* ------------------------------------------------------------------ */

    @ExceptionHandler(ResourceAlreadyExistsException.class)
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation (
            DataIntegrityViolationException exception,
            HttpServletRequest request
                                                       ) {
        return buildError(
                HttpStatus.CONFLICT,
                "The request conflicts with the current state of the resource.",
                request,
                List.of()
                         );
    }

    /* ------------------------------------------------------------------ */
    /* Not found                                                           */
    /* ------------------------------------------------------------------ */

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound (
            ResourceNotFoundException exception,
            HttpServletRequest request
                                          ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request,
                List.of()
                         );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandler (
            NoHandlerFoundException exception,
            HttpServletRequest request
                                          ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                "No endpoint " + exception.getHttpMethod() + " " + exception.getRequestURL(),
                request,
                List.of()
                         );
    }

    /* ------------------------------------------------------------------ */
    /* Business rule violations                                            */
    /* ------------------------------------------------------------------ */

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule (
            BusinessRuleException exception,
            HttpServletRequest request
                                         ) {
        return buildError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage(),
                request,
                List.of()
                         );
    }

    /* ------------------------------------------------------------------ */
    /* Uploads and media                                                   */
    /* ------------------------------------------------------------------ */

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleInvalidRequest (
            InvalidRequestException exception,
            HttpServletRequest request
                                             ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request,
                List.of()
                         );
    }

    @ExceptionHandler(UnsupportedMediaException.class)
    public ResponseEntity<ApiError> handleUnsupportedMedia (
            UnsupportedMediaException exception,
            HttpServletRequest request
                                            ) {
        return buildError(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                exception.getMessage(),
                request,
                List.of()
                         );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaTypeNotSupported (
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
                                               ) {
        return buildError(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content type " + exception.getContentType() + " is not supported by this endpoint.",
                request,
                List.of()
                         );
    }

    @ExceptionHandler(PayloadTooLargeException.class)
    public ResponseEntity<ApiError> handlePayloadTooLarge (
            PayloadTooLargeException exception,
            HttpServletRequest request
                                           ) {
        return buildError(
                HttpStatus.PAYLOAD_TOO_LARGE,
                exception.getMessage(),
                request,
                List.of()
                         );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSizeExceeded (
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
                                               ) {
        return buildError(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "The uploaded file exceeds the maximum request size allowed by the server.",
                request,
                List.of()
                         );
    }

    /**
     * Multipart resolution failures surface as {@link MultipartException} subclasses
     * (malformed bodies, truncated parts, I/O errors while spooling to disk).
     * {@link MaxUploadSizeExceededException} is handled above and takes precedence.
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiError> handleMultipart (
            MultipartException exception,
            HttpServletRequest request
                                         ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "The multipart request could not be parsed.",
                request,
                List.of()
                         );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiError> handleMissingPart (
            MissingServletRequestPartException exception,
            HttpServletRequest request
                                           ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Required file part '" + exception.getRequestPartName() + "' is missing.",
                request,
                List.of(new ApiError.FieldError(
                        exception.getRequestPartName(),
                        "This file part is required."
                ))
                         );
    }

    /* ------------------------------------------------------------------ */
    /* Request validation                                                  */
    /* ------------------------------------------------------------------ */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation (
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation (
            ConstraintViolationException exception,
            HttpServletRequest request
                                               ) {
        List<ApiError.FieldError> fields =
                exception.getConstraintViolations()
                        .stream()
                        .map(violation ->
                                     new ApiError.FieldError(
                                             lastNode(violation.getPropertyPath()
                                                                         .toString()),
                                             violation.getMessage()
                                     )
                            )
                        .toList();
        return buildError(
                HttpStatus.BAD_REQUEST,
                "One or more parameters are invalid.",
                request,
                fields
                         );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter (
            MissingServletRequestParameterException exception,
            HttpServletRequest request
                                                   ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Required parameter '" + exception.getParameterName() + "' is missing.",
                request,
                List.of(new ApiError.FieldError(
                        exception.getParameterName(),
                        "This parameter is required."
                ))
                         );
    }

    /**
     * Raised when a {@code @RequestParam}/{@code @PathVariable} cannot be converted,
     * for example {@code ?scope=NOT_A_SCOPE} bound to a {@code MediaScope} enum.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch (
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
                                            ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Parameter '" + exception.getName() + "' has an invalid value.",
                request,
                List.of(new ApiError.FieldError(
                        exception.getName(),
                        "Value '" + exception.getValue() + "' is not valid for this parameter."
                ))
                         );
    }

    @ExceptionHandler({
            TypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> handleUnreadable (
            Exception exception,
            HttpServletRequest request
                                         ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "The request body or parameters could not be read.",
                request,
                List.of()
                         );
    }

    @ExceptionHandler({
            PropertyReferenceException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ApiError> handleBadRequestShape (
            Exception exception,
            HttpServletRequest request
                                         ) {
        String message =
                exception instanceof PropertyReferenceException
                        ? "Invalid sorting property provided."
                        : "HTTP method is not supported for this endpoint.";
        return buildError(
                HttpStatus.BAD_REQUEST,
                message,
                request,
                List.of()
                         );
    }

    /* ------------------------------------------------------------------ */
    /* Security                                                            */
    /* ------------------------------------------------------------------ */

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials (
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication (
            AuthenticationException exception,
            HttpServletRequest request
                                         ) {
        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource.",
                request,
                List.of()
                         );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied (
            AuthorizationDeniedException exception,
            HttpServletRequest request
                                         ) {
        return buildError(
                HttpStatus.FORBIDDEN,
                "Access denied.",
                request,
                List.of()
                         );
    }

    /* ------------------------------------------------------------------ */
    /* Helpers                                                             */
    /* ------------------------------------------------------------------ */

    private String lastNode (
            String path
                        ) {
        int index = path.lastIndexOf('.');
        return index < 0
                ? path
                : path.substring(index + 1);
    }

    private ResponseEntity<ApiError> buildError (
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

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidSortProperty (Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Invalid sorting property provided.");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}