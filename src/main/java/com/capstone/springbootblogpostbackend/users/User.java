package com.capstone.springbootblogpostbackend.users;

import com.capstone.springbootblogpostbackend.posts.Post;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profileImageUrl;

    @Builder.Default
    @OneToMany(mappedBy = "author")
    @JsonManagedReference
    private List<Post> posts = new ArrayList<>();
}
