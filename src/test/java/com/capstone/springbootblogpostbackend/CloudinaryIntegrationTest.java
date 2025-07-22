package com.capstone.springbootblogpostbackend;

import com.capstone.springbootblogpostbackend.config.CloudinaryConfig;
import com.capstone.springbootblogpostbackend.service.CloudinaryService;
import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "cloudinary.cloud-name=test-cloud",
    "cloudinary.api-key=test-key",
    "cloudinary.api-secret=test-secret"
})
public class CloudinaryIntegrationTest {

    @Autowired
    private CloudinaryConfig cloudinaryConfig;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Test
    void testCloudinaryConfig() {
        Cloudinary cloudinary = cloudinaryConfig.cloudinary();
        assertNotNull(cloudinary);
        assertEquals("test-cloud", cloudinary.config.cloudName);
        assertEquals("test-key", cloudinary.config.apiKey);
        assertEquals("test-secret", cloudinary.config.apiSecret);
    }

    @Test
    void testCloudinaryServiceBean() {
        assertNotNull(cloudinaryService);
    }
} 