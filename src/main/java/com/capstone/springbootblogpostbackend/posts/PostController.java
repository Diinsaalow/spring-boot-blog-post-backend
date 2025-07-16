package com.capstone.springbootblogpostbackend.posts;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.springbootblogpostbackend.exception.BlogException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody PostDTO postDTO, Authentication authentication) {
        String email = authentication.getName();
        Post createdPost = postService.createPost(postDTO, email);
        return ResponseEntity.ok(postService.mapToDTO(createdPost));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostDTO postDTO,
            Authentication authentication) {
        String email = authentication.getName();
        Post updatedPost = postService.updatePost(id, postDTO, email);
        return ResponseEntity.ok(postService.mapToDTO(updatedPost));
    }

    @DeleteMapping("/{id}")
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