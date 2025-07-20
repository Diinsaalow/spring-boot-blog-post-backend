package com.capstone.springbootblogpostbackend.comments;

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
import org.springframework.web.bind.annotation.RestController;

import com.capstone.springbootblogpostbackend.auth.CommentAccess;
import com.capstone.springbootblogpostbackend.exception.BlogException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO comment = commentService.getCommentById(id)
                .orElseThrow(() -> BlogException.notFound("Comment", id));
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long postId,
            @Valid @RequestBody CommentDTO commentDTO,
            Authentication authentication) {
        String email = authentication.getName();
        CommentDTO createdComment = commentService.createComment(commentDTO, postId, email);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/{id}")
    @CommentAccess
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO,
            Authentication authentication) {
        String email = authentication.getName();
        CommentDTO updatedComment = commentService.updateComment(id, commentDTO, email);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    @CommentAccess
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        commentService.deleteComment(id, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(commentService.getCommentsByAuthor(authorId));
    }
}