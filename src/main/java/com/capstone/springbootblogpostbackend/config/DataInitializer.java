package com.capstone.springbootblogpostbackend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.capstone.springbootblogpostbackend.users.Role;
import com.capstone.springbootblogpostbackend.users.User;
import com.capstone.springbootblogpostbackend.users.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        if (!userRepository.existsByEmail("admin@example.com")) {
            User adminUser = User.builder()
                    .fullName("Admin User")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(adminUser);
            System.out.println("Admin user created successfully");
        }

        // Create a regular user if it doesn't exist
        if (!userRepository.existsByEmail("user@example.com")) {
            User regularUser = User.builder()
                    .fullName("Regular User")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(regularUser);
            System.out.println("Regular user created successfully");
        }
    }
}