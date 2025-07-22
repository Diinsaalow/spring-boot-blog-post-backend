package com.capstone.springbootblogpostbackend.controller;

import com.capstone.springbootblogpostbackend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageUploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload/post-thumbnail")
    public ResponseEntity<Map<String, String>> uploadPostThumbnail(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file, "blog-post-thumbnails");
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Post thumbnail uploaded successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload post thumbnail: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/upload/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file, "user-profile-images");
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Profile image uploaded successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload profile image: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 