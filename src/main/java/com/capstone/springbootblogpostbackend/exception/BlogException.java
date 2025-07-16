package com.capstone.springbootblogpostbackend.exception;

import org.springframework.http.HttpStatus;

public class BlogException extends RuntimeException {
    private final HttpStatus status;
    private final String error;

    public BlogException(String message, HttpStatus status, String error) {
        super(message);
        this.status = status;
        this.error = error;
    }

    public BlogException(String message, HttpStatus status) {
        this(message, status, status.getReasonPhrase());
    }

    // Static factory methods for common error types
    public static BlogException notFound(String message) {
        return new BlogException(message, HttpStatus.NOT_FOUND);
    }

    public static BlogException notFound(String resource, Long id) {
        return new BlogException(resource + " not found with id: " + id, HttpStatus.NOT_FOUND);
    }

    public static BlogException badRequest(String message) {
        return new BlogException(message, HttpStatus.BAD_REQUEST);
    }

    public static BlogException unauthorized(String message) {
        return new BlogException(message, HttpStatus.UNAUTHORIZED);
    }

    public static BlogException forbidden(String message) {
        return new BlogException(message, HttpStatus.FORBIDDEN);
    }

    public static BlogException internalServerError(String message) {
        return new BlogException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static BlogException conflict(String message) {
        return new BlogException(message, HttpStatus.CONFLICT);
    }

    // Getters
    public HttpStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public int getStatusCode() {
        return status.value();
    }
} 