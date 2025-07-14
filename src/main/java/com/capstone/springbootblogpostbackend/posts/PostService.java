package com.capstone.springbootblogpostbackend.posts;

import com.capstone.springbootblogpostbackend.users.User;
import com.capstone.springbootblogpostbackend.users.UserRepository;
import com.capstone.springbootblogpostbackend.users.UserDTO;
import com.capstone.springbootblogpostbackend.comments.CommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
        private final PostRepository postRepository;
        private final UserRepository userRepository;

        public List<PostDTO> getAllPosts() {
                return postRepository.findAll().stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList());
        }

        private PostDTO mapToDTO(Post post) {
                UserDTO authorDTO = UserDTO.builder()
                                .id(post.getAuthor().getId())
                                .username(post.getAuthor().getUsername())
                                .email(post.getAuthor().getEmail())
                                .role(post.getAuthor().getRole().name()) // Convert Role enum to String
                                .build();

                List<CommentDTO> commentDTOs = post.getComments() == null ? List.of()
                                : post.getComments().stream()
                                                .map(comment -> CommentDTO.builder()
                                                                .id(comment.getId())
                                                                .content(comment.getContent())
                                                                .postId(post.getId())
                                                                .createdAt(comment.getCreatedAt())
                                                                .updatedAt(comment.getUpdatedAt())
                                                                .build())
                                                .collect(Collectors.toList());

                return PostDTO.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .thumbnailUrl(post.getThumbnailUrl())
                                .author(authorDTO)
                                .comments(commentDTOs)
                                .createdAt(post.getCreatedAt())
                                .updatedAt(post.getUpdatedAt())
                                .build();
        }

        public Optional<Post> getPostById(Long id) {
                return postRepository.findById(id);
        }

        public Post createPost(PostDTO postDTO, String username) {
                User author = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Post post = Post.builder()
                                .title(postDTO.getTitle())
                                .content(postDTO.getContent())
                                .thumbnailUrl(postDTO.getThumbnailUrl())
                                .author(author)
                                .build();

                return postRepository.save(post);
        }

        public Post updatePost(Long id, PostDTO postDTO, String username) {
                Post post = postRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Post not found"));

                User author = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Check if the user is the author or an admin
                if (!post.getAuthor().getId().equals(author.getId()) &&
                                !author.getRole().name().equals("ADMIN")) {
                        throw new RuntimeException("You can only update your own posts");
                }

                post.setTitle(postDTO.getTitle());
                post.setContent(postDTO.getContent());
                post.setThumbnailUrl(postDTO.getThumbnailUrl());

                return postRepository.save(post);
        }

        public void deletePost(Long id, String username) {
                Post post = postRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Post not found"));

                User author = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Check if the user is the author or an admin
                if (!post.getAuthor().getId().equals(author.getId()) &&
                                !author.getRole().name().equals("ADMIN")) {
                        throw new RuntimeException("You can only delete your own posts");
                }

                postRepository.delete(post);
        }

        public List<Post> getPostsByAuthor(Long authorId) {
                return postRepository.findByAuthorId(authorId);
        }

        public List<Post> searchPostsByTitle(String title) {
                return postRepository.findByTitleContainingIgnoreCase(title);
        }
}