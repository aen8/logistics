package com.logistics.user_service.service;

import com.logistics.user_service.dtos.UpdateUserRequest;
import com.logistics.user_service.dtos.response.UserResponse;
import com.logistics.user_service.exceptions.UserNotFoundException;
import com.logistics.user_service.model.*;
import com.logistics.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    //Get the userRepo
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        // Update specific fields based on user type
        if (user instanceof Client && request.getNom() != null) {
            ((Client) user).setNom(request.getNom());
            ((Client) user).setTelephone(request.getTelephone());
        } else if (user instanceof Livreur && request.getNom() != null) {
            ((Livreur) user).setNom(request.getNom());
            ((Livreur) user).setTelephone(request.getTelephone());
        } else if (user instanceof Admin && request.getNom() != null) {
            ((Admin) user).setNom(request.getNom());
            ((Admin) user).setTelephone(request.getTelephone());
        } else if (user instanceof Manager && request.getNom() != null) {
            ((Manager) user).setNom(request.getNom());
            ((Manager) user).setTelephone(request.getTelephone());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", id);

        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully with ID: {}", id);
    }

    private UserResponse mapToUserResponse(User user) {
        String nom = "Unknown";
        String telephone = "N/A";

        if (user instanceof Client) {
            nom = ((Client) user).getNom();
            telephone = ((Client) user).getTelephone();
        } else if (user instanceof Livreur) {
            nom = ((Livreur) user).getNom();
            telephone = ((Livreur) user).getTelephone();
        } else if (user instanceof Admin) {
            nom = ((Admin) user).getNom();
            telephone = ((Admin) user).getTelephone();
        } else if (user instanceof Manager) {
            nom = ((Manager) user).getNom();
            telephone = ((Manager) user).getTelephone();
        }

        return UserResponse.builder()
                .idUser(user.getIdUser())
                .email(user.getEmail())
                .role(user.getRole())
                .nom(nom)
                .telephone(telephone)
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}