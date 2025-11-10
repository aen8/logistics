package com.logistics.delivery_service.security;

import com.logistics.delivery_service.client.UserServiceClient;
import com.logistics.delivery_service.model.Livraison;
import com.logistics.delivery_service.repository.LivraisonRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component("deliverySecurity")
@RequiredArgsConstructor
@Slf4j
public class DeliverySecurity {

    private final LivraisonRepository livraisonRepository;
    private final UserServiceClient userServiceClient;

    /**
     * Vérifie si l'utilisateur peut accéder à cette livraison
     */
    public boolean canAccessDelivery(Long deliveryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Admin et Manager peuvent tout voir
        if (hasAnyRole(authentication, "ADMIN", "MANAGER")) {
            return true;
        }

        String email = authentication.getName();

        // Récupérer la livraison
        Optional<Livraison> livraisonOpt = livraisonRepository.findById(deliveryId);
        if (livraisonOpt.isEmpty()) {
            return false;
        }

        Livraison livraison = livraisonOpt.get();

        // Vérifier si c'est le client de cette livraison
        if (hasRole(authentication, "CLIENT")) {
            // TODO: Récupérer l'userId depuis user-service via email
            // et comparer avec livraison.getClientId()
            return true; // Pour l'instant, autoriser
        }

        // Vérifier si c'est le livreur assigné
        if (hasRole(authentication, "LIVREUR")) {
            try {
                // Récupérer le livreur depuis user-service
                // TODO: Implémenter une méthode getUserByEmail dans UserServiceClient
                return true; // Pour l'instant, autoriser
            } catch (FeignException e) {
                log.error("Failed to verify driver", e);
                return false;
            }
        }

        return false;
    }

    /**
     * Vérifie si l'utilisateur peut modifier le statut de cette livraison
     */
    public boolean canUpdateDeliveryStatus(Long deliveryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Admin et Manager peuvent toujours modifier
        if (hasAnyRole(authentication, "ADMIN", "MANAGER")) {
            return true;
        }

        // Livreur peut modifier uniquement ses livraisons
        if (hasRole(authentication, "LIVREUR")) {
            Optional<Livraison> livraisonOpt = livraisonRepository.findById(deliveryId);
            if (livraisonOpt.isPresent()) {
                // TODO: Vérifier que c'est bien le livreur assigné
                return true;
            }
        }

        return false;
    }

    /**
     * Vérifie si l'utilisateur peut annuler cette livraison
     */
    public boolean canCancelDelivery(Long deliveryId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Admin et Manager peuvent toujours annuler
        if (hasAnyRole(authentication, "ADMIN", "MANAGER")) {
            return true;
        }

        // Client peut annuler sa propre livraison
        if (hasRole(authentication, "CLIENT")) {
            Optional<Livraison> livraisonOpt = livraisonRepository.findById(deliveryId);
            if (livraisonOpt.isPresent()) {
                // TODO: Vérifier que c'est bien le client de cette livraison
                return true;
            }
        }

        return false;
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + role));
    }

    private boolean hasAnyRole(Authentication authentication, String... roles) {
        for (String role : roles) {
            if (hasRole(authentication, role)) {
                return true;
            }
        }
        return false;
    }
}