package com.capstone.springbootblogpostbackend.posts;

import java.time.LocalDateTime;
import java.util.List;

import com.capstone.springbootblogpostbackend.comments.CommentDTO;
import com.capstone.springbootblogpostbackend.users.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Content cannot be blank")
    @NotNull(message = "Content cannot be null")
    private String content;
    private String thumbnailUrl;
    @Builder.Default
    private Boolean isFeatured = false;
    private UserDTO author;
    private List<CommentDTO> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}