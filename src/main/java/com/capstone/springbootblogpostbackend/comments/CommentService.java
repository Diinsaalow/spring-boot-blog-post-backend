package com.capstone.springbootblogpostbackend.comments;

import com.capstone.springbootblogpostbackend.posts.Post;
import com.capstone.springbootblogpostbackend.posts.PostRepository;
import com.capstone.springbootblogpostbackend.users.User;
import com.capstone.springbootblogpostbackend.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public Comment createComment(CommentDTO commentDTO, Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .content(commentDTO.getContent())
                .post(post)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, CommentDTO commentDTO, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user is the author or an admin
        if (!comment.getAuthor().getId().equals(author.getId()) &&
                !author.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("You can only update your own comments");
        }

        comment.setContent(commentDTO.getContent());

        return commentRepository.save(comment);
    }

    public void deleteComment(Long id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user is the author or an admin
        if (!comment.getAuthor().getId().equals(author.getId()) &&
                !author.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    public List<Comment> getCommentsByAuthor(Long authorId) {
        return commentRepository.findByAuthorId(authorId);
    }
}