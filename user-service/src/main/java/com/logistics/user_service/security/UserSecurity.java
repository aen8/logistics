package com.logistics.user_service.security;

import com.logistics.user_service.model.User;
import com.logistics.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component("userSecurity")  //  Le nom "userSecurity" correspond à @userSecurity
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    /**
     * Vérifie si l'utilisateur authentifié est le propriétaire de la ressource
     * @param userId L'ID de l'utilisateur à vérifier
     * @return true si l'utilisateur est le propriétaire, false sinon
     */
    public boolean isOwner(Long userId) {
        // Récupérer l'authentification actuelle
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Vérifier si l'utilisateur est authentifié
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Récupérer le principal (UserDetails)
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return false;
        }

        // Récupérer l'email de l'utilisateur authentifié
        String email = ((UserDetails) principal).getUsername();
        User user = userRepository.findByEmail(email).orElse(null);

        // Vérifier si l'ID correspond
        return user != null && user.getIdUser().equals(userId);
    }
}