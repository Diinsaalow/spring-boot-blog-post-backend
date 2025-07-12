package com.capstone.springbootblogpostbackend.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthorId(Long authorId);

    List<Post> findByTitleContainingIgnoreCase(String title);
}