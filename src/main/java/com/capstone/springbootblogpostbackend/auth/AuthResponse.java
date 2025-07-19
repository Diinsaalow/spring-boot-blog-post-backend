package com.capstone.springbootblogpostbackend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long id;
    private String fullName;
    private String email;
    private String profileImageUrl;
    private String role;
    private String message;
}