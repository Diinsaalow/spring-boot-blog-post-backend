package com.capstone.springbootblogpostbackend.users;

import com.capstone.springbootblogpostbackend.exception.BlogException;
import com.capstone.springbootblogpostbackend.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToDTO);
    }

        public UserDTO updateUserProfile(Long userId, String email, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BlogException.notFound("User", userId));
        
        // Verify the user is updating their own profile
        if (!user.getEmail().equals(email)) {
            throw BlogException.forbidden("Unauthorized to update this user profile");
        }
        
        if (userUpdateRequest.getFullName() != null && !userUpdateRequest.getFullName().trim().isEmpty()) {
            user.setFullName(userUpdateRequest.getFullName());
        }
        
        if (userUpdateRequest.getProfileImage() != null) {
            // Upload profile image to Cloudinary
            String profileImageUrl = cloudinaryService.uploadImage(
                userUpdateRequest.getProfileImage(), 
                "user-profile-images"
            );
            user.setProfileImageUrl(profileImageUrl);
        }
        
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    public UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .postIds(user.getPosts() != null ? user.getPosts().stream().map(post -> post.getId()).toList()
                        : List.of())
                .build();
    }
}