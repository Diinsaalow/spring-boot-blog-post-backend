package com.capstone.springbootblogpostbackend.users;

import com.capstone.springbootblogpostbackend.posts.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profileImageUrl;

    @OneToMany(mappedBy = "author")
    private List<Post> posts = new ArrayList<>();
}

enum Role {
    USER,
    ADMIN
}
