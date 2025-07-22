package com.capstone.springbootblogpostbackend.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

        private final UserService userService;

        @GetMapping("/profile")
        public ResponseEntity<UserDTO> getCurrentUserProfile(Authentication authentication) {
                String email = authentication.getName();
                UserDTO userDTO = userService.getUserByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return ResponseEntity.ok(userDTO);
        }

        @PutMapping(value = "/profile", consumes = "multipart/form-data")
        public ResponseEntity<UserDTO> updateUserProfile(
                        @RequestPart(value = "fullName", required = false) String fullName,
                        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                        Authentication authentication) {
                String email = authentication.getName();

                // Get current user to get their ID
                UserDTO currentUser = userService.getUserByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                                .fullName(fullName)
                                .profileImage(profileImage)
                                .build();

                UserDTO updatedUser = userService.updateUserProfile(
                                currentUser.getId(),
                                email,
                                userUpdateRequest);

                return ResponseEntity.ok(updatedUser);
        }

        @GetMapping("/{id}")
        public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
                UserDTO userDTO = userService.getUserById(id)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return ResponseEntity.ok(userDTO);
        }
}