package com.capstone.springbootblogpostbackend.posts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDTO {
    private String title;
    private String content;
    private String thumbnailUrl;
    private Boolean isFeatured;
} 