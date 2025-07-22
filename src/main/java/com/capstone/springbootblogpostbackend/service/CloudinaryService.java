package com.capstone.springbootblogpostbackend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) {
        try {
            Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto"
            );
            
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            String secureUrl = (String) result.get("secure_url");
            
            log.info("Image uploaded successfully to Cloudinary: {}", secureUrl);
            return secureUrl;
            
        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted successfully from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Error deleting image from Cloudinary", e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }
} 