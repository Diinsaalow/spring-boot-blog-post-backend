package com.capstone.springbootblogpostbackend.posts;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.capstone.springbootblogpostbackend.auth.AdminOnly;
import com.capstone.springbootblogpostbackend.exception.BlogException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id)
                .orElseThrow(() -> BlogException.notFound("Post", id));
        return ResponseEntity.ok(postService.mapToDTO(post));
    }

        @PostMapping(consumes = "multipart/form-data")
    @AdminOnly
    public ResponseEntity<PostDTO> createPost(
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
            @RequestPart(value = "isFeatured", required = false) String isFeatured,
            Authentication authentication) {
        String email = authentication.getName();
        
        // Validate required fields
        if (title == null || title.trim().isEmpty()) {
            throw BlogException.badRequest("Title is required");
        }
        if (content == null || content.trim().isEmpty()) {
            throw BlogException.badRequest("Content is required");
        }
        if (thumbnailImage == null || thumbnailImage.isEmpty()) {
            throw BlogException.badRequest("Thumbnail image is required");
        }
        
        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .title(title)
                .content(content)
                .thumbnailImage(thumbnailImage)
                .isFeatured(isFeatured != null ? Boolean.parseBoolean(isFeatured) : false)
                .build();
        
        Post createdPost = postService.createPost(postCreateRequest, email);
        return ResponseEntity.ok(postService.mapToDTO(createdPost));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @AdminOnly
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestPart(value = "title", required = false) String title,
            @RequestPart(value = "content", required = false) String content,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "isFeatured", required = false) String isFeatured,
            Authentication authentication) {
        String email = authentication.getName();

        PostUpdateRequest postUpdateRequest = PostUpdateRequest.builder()
                .title(title)
                .content(content)
                .thumbnailImage(thumbnailImage)
                .isFeatured(isFeatured != null ? Boolean.parseBoolean(isFeatured) : null)
                .build();

        Post updatedPost = postService.updatePost(id, postUpdateRequest, email);
        return ResponseEntity.ok(postService.mapToDTO(updatedPost));
    }

    @DeleteMapping("/{id}")
    @AdminOnly
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        postService.deletePost(id, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostDTO>> getPostsByAuthor(@PathVariable Long authorId) {
        List<Post> posts = postService.getPostsByAuthor(authorId);
        List<PostDTO> postDTOs = posts.stream().map(postService::mapToDTO).toList();
        return ResponseEntity.ok(postDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String title) {
        List<Post> posts = postService.searchPostsByTitle(title);
        List<PostDTO> postDTOs = posts.stream().map(postService::mapToDTO).toList();
        return ResponseEntity.ok(postDTOs);
    }
}