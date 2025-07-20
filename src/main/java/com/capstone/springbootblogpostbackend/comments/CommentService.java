package com.capstone.springbootblogpostbackend.comments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.capstone.springbootblogpostbackend.exception.BlogException;
import com.capstone.springbootblogpostbackend.posts.Post;
import com.capstone.springbootblogpostbackend.posts.PostRepository;
import com.capstone.springbootblogpostbackend.users.User;
import com.capstone.springbootblogpostbackend.users.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
        private final CommentRepository commentRepository;
        private final PostRepository postRepository;
        private final UserRepository userRepository;

        public List<CommentDTO> getCommentsByPostId(Long postId) {
                return commentRepository.findByPostId(postId).stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        public Optional<CommentDTO> getCommentById(Long id) {
                return commentRepository.findById(id).map(this::mapToDTO);
        }

        public CommentDTO createComment(CommentDTO commentDTO, Long postId, String email) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> BlogException.notFound("Post", postId));

                User author = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Comment comment = Comment.builder()
                                .content(commentDTO.getContent())
                                .post(post)
                                .author(author)
                                .build();

                Comment saved = commentRepository.save(comment);
                return mapToDTO(saved);
        }

        public CommentDTO updateComment(Long id, CommentDTO commentDTO, String email) {
                Comment comment = commentRepository.findById(id)
                                .orElseThrow(() -> BlogException.notFound("Comment", id));

                User author = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                comment.setContent(commentDTO.getContent());

                Comment saved = commentRepository.save(comment);
                return mapToDTO(saved);
        }

        public void deleteComment(Long id, String email) {
                Comment comment = commentRepository.findById(id)
                                .orElseThrow(() -> BlogException.notFound("Comment", id));

                User author = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                commentRepository.delete(comment);
        }

        public List<CommentDTO> getCommentsByAuthor(Long authorId) {
                return commentRepository.findByAuthorId(authorId).stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        /**
         * Check if the given user is the author of the comment
         * This method is used by the @CommentAccess annotation for method-level security
         */
        public boolean isCommentAuthor(Long commentId, String userEmail) {
                try {
                        Comment comment = commentRepository.findById(commentId)
                                        .orElse(null);
                        if (comment == null) {
                                return false;
                        }
                        
                        User user = userRepository.findByEmail(userEmail)
                                        .orElse(null);
                        if (user == null) {
                                return false;
                        }
                        
                        return comment.getAuthor().getId().equals(user.getId());
                } catch (Exception e) {
                        return false;
                }
        }

        private CommentDTO mapToDTO(Comment comment) {
                return CommentDTO.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .postId(comment.getPost().getId())
                                .author(com.capstone.springbootblogpostbackend.users.UserDTO.builder()
                                                .id(comment.getAuthor().getId())
                                                .fullName(comment.getAuthor().getFullName())
                                                .email(comment.getAuthor().getEmail())
                                                .profileImageUrl(comment.getAuthor().getProfileImageUrl())
                                                .role(comment.getAuthor().getRole().name())
                                                .build())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .build();
        }
}