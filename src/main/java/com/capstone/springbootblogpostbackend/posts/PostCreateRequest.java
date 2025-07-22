package com.capstone.springbootblogpostbackend.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @NotNull(message = "Content cannot be null")
    private String content;

    @NotNull(message = "Thumbnail image is required")
    private MultipartFile thumbnailImage;

    // Custom validation method
    public boolean isThumbnailImageValid() {
        return thumbnailImage != null && !thumbnailImage.isEmpty();
    }

    @Builder.Default
    private Boolean isFeatured = false;
}