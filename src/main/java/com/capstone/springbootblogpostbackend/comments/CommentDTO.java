package com.capstone.springbootblogpostbackend.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.capstone.springbootblogpostbackend.users.UserDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private Long postId;
    private UserDTO author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}