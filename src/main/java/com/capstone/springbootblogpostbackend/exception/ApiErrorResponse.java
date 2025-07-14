package com.capstone.springbootblogpostbackend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private int statusCode;
    private String message;
    private String error;
    private String path;
    private String timestamp;
}