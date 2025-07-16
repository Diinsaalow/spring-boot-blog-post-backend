package com.capstone.springbootblogpostbackend.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String message) {
        super(message);
    }
    
    public PostNotFoundException(Long postId) {
        super("Post not found with id: " + postId);
    }
} 