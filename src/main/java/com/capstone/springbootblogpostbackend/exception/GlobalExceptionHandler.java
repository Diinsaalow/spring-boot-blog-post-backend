package com.capstone.springbootblogpostbackend.exception;

import java.lang.reflect.Field;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Utility to extract required fields from a DTO class
    private static List<String> getRequiredFields(Class<?> dtoClass) {
        List<String> requiredFields = new ArrayList<>();
        for (Field field : dtoClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(NotBlank.class) || field.isAnnotationPresent(NotNull.class)) {
                requiredFields.add(field.getName());
            }
        }
        return requiredFields;
    }

    // Map endpoint paths to DTO classes
    private static final Map<String, Class<?>> endpointDtoMap = new HashMap<>() {
        {
            put("/api/v1/auth/login", com.capstone.springbootblogpostbackend.auth.AuthRequest.class);
            put("/api/v1/auth/register", com.capstone.springbootblogpostbackend.auth.RegisterRequest.class);
            put("/api/v1/posts", com.capstone.springbootblogpostbackend.posts.PostDTO.class);
            put("/api/v1/comments/post/{postId}", com.capstone.springbootblogpostbackend.comments.CommentDTO.class);
        }
    };

    private ApiErrorResponse buildErrorResponse(int statusCode, String message, String error, String path) {
        return new ApiErrorResponse(
                statusCode,
                message,
                error,
                path,
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    // Handle our custom BlogException
    @ExceptionHandler(BlogException.class)
    public ResponseEntity<ApiErrorResponse> handleBlogException(BlogException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getStatus()).body(
                buildErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getError(), request.getRequestURI()));
    }

    // Handle validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        StringBuilder missingFields = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String fieldName = ((FieldError) err).getField();
            if (missingFields.length() > 0)
                missingFields.append(", ");
            missingFields.append(fieldName);
        });
        String message;
        if (missingFields.length() > 0) {
            message = missingFields + " are required";
        } else {
            if (!ex.getBindingResult().getTarget().getClass().getSimpleName().equals("Object")) {
                List<String> requiredFields = getRequiredFields(ex.getBindingResult().getTarget().getClass());
                if (!requiredFields.isEmpty()) {
                    message = "All fields are required: " + String.join(", ", requiredFields);
                } else {
                    message = "All fields are required";
                }
            } else {
                message = "All fields are required";
            }
        }
        return ResponseEntity.badRequest().body(
                buildErrorResponse(400, message, "Bad Request", request.getRequestURI()));
    }

    // Handle constraint violation exceptions
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex,
            HttpServletRequest request) {
        StringBuilder violations = new StringBuilder();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            if (violations.length() > 0) violations.append(", ");
            violations.append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
        }
        String message = violations.length() > 0 ? violations.toString() : "Validation failed";
        return ResponseEntity.badRequest().body(
                buildErrorResponse(400, message, "Bad Request", request.getRequestURI()));
    }

    // Handle HTTP message not readable exceptions
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        String path = request.getRequestURI();
        Class<?> dtoClass = endpointDtoMap.get(path);
        String message;
        if (dtoClass != null) {
            List<String> requiredFields = getRequiredFields(dtoClass);
            if (!requiredFields.isEmpty()) {
                message = "All fields are required: " + String.join(", ", requiredFields);
            } else {
                message = "All fields are required";
            }
        } else {
            message = "Invalid request body format";
        }
        return ResponseEntity.badRequest().body(
                buildErrorResponse(400, message, "Bad Request", path));
    }

    // Handle method argument type mismatch exceptions
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid parameter type for '" + ex.getName() + "': expected " + 
                        ex.getRequiredType().getSimpleName() + ", got " + 
                        (ex.getValue() != null ? ex.getValue().getClass().getSimpleName() : "null");
        return ResponseEntity.badRequest().body(
                buildErrorResponse(400, message, "Bad Request", request.getRequestURI()));
    }

    // Handle 404 Not Found exceptions
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex,
            HttpServletRequest request) {
        String message = "Endpoint not found: " + ex.getHttpMethod() + " " + ex.getRequestURL();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildErrorResponse(404, message, "Not Found", request.getRequestURI()));
    }

    // Handle authentication and authorization exceptions
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildErrorResponse(404, ex.getMessage(), "Not Found", request.getRequestURI()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildErrorResponse(401, "Invalid username or password", "Unauthorized", request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildErrorResponse(403, "Access denied", "Forbidden", request.getRequestURI()));
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildErrorResponse(401, "Authentication required", "Unauthorized", request.getRequestURI()));
    }

    // Handle legacy exceptions for backward compatibility
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePostNotFoundException(PostNotFoundException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                buildErrorResponse(404, ex.getMessage(), "Not Found", request.getRequestURI()));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                buildErrorResponse(403, ex.getMessage(), "Forbidden", request.getRequestURI()));
    }

    // Handle generic runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                buildErrorResponse(400, ex.getMessage(), "Bad Request", request.getRequestURI()));
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildErrorResponse(500, "An unexpected error occurred", "Internal Server Error",
                        request.getRequestURI()));
    }
}