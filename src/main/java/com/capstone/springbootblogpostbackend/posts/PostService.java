package com.capstone.springbootblogpostbackend.posts;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.capstone.springbootblogpostbackend.comments.CommentDTO;
import com.capstone.springbootblogpostbackend.exception.BlogException;
import com.capstone.springbootblogpostbackend.users.User;
import com.capstone.springbootblogpostbackend.users.UserDTO;
import com.capstone.springbootblogpostbackend.users.UserRepository;

import lombok.RequiredArgsConstructor;

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

        public PostDTO mapToDTO(Post post) {
                UserDTO authorDTO = UserDTO.builder()
                                .id(post.getAuthor().getId())
                                .fullName(post.getAuthor().getFullName())
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
                                                                .author(com.capstone.springbootblogpostbackend.users.UserDTO
                                                                                .builder()
                                                                                .id(comment.getAuthor().getId())
                                                                                .fullName(comment.getAuthor()
                                                                                                .getFullName())
                                                                                .email(comment.getAuthor().getEmail())
                                                                                .profileImageUrl(comment.getAuthor()
                                                                                                .getProfileImageUrl())
                                                                                .role(comment.getAuthor().getRole()
                                                                                                .name())
                                                                                .build())
                                                                .build())
                                                .collect(Collectors.toList());

                return PostDTO.builder()
                                .id(post.getId())
                                .title(post.getTitle())
                                .content(post.getContent())
                                .thumbnailUrl(post.getThumbnailUrl())
                                .isFeatured(post.getIsFeatured())
                                .author(authorDTO)
                                .comments(commentDTOs)
                                .createdAt(post.getCreatedAt())
                                .updatedAt(post.getUpdatedAt())
                                .build();
        }

        public Optional<Post> getPostById(Long id) {
                return postRepository.findById(id);
        }

        public Post createPost(PostDTO postDTO, String email) {
                User author = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                Post post = Post.builder()
                                .title(postDTO.getTitle())
                                .content(postDTO.getContent())
                                .thumbnailUrl(postDTO.getThumbnailUrl())
                                .isFeatured(postDTO.getIsFeatured() != null ? postDTO.getIsFeatured() : false)
                                .author(author)
                                .build();

                return postRepository.save(post);
        }

        public Post updatePost(Long id, PostUpdateDTO postUpdateDTO, String email) {
                Post post = postRepository.findById(id)
                                .orElseThrow(() -> BlogException.notFound("Post", id));

                User author = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Check if the user is the author or an admin
                if (!post.getAuthor().getId().equals(author.getId()) &&
                                !author.getRole().name().equals("ADMIN")) {
                        throw BlogException.forbidden("You can only update your own posts");
                }

                // Only update fields that are provided (not null)
                if (postUpdateDTO.getTitle() != null) {
                        post.setTitle(postUpdateDTO.getTitle());
                }
                if (postUpdateDTO.getContent() != null) {
                        post.setContent(postUpdateDTO.getContent());
                }
                if (postUpdateDTO.getThumbnailUrl() != null) {
                        post.setThumbnailUrl(postUpdateDTO.getThumbnailUrl());
                }
                if (postUpdateDTO.getIsFeatured() != null) {
                        post.setIsFeatured(postUpdateDTO.getIsFeatured());
                }

                return postRepository.save(post);
        }

        public void deletePost(Long id, String email) {
                Post post = postRepository.findById(id)
                                .orElseThrow(() -> BlogException.notFound("Post", id));

                User author = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                // Check if the user is the author or an admin
                if (!post.getAuthor().getId().equals(author.getId()) &&
                                !author.getRole().name().equals("ADMIN")) {
                        throw BlogException.forbidden("You can only delete your own posts");
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