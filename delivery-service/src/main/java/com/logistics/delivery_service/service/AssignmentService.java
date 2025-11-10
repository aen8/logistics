package com.logistics.delivery_service.service;



import com.logistics.delivery_service.client.UserServiceClient;
import com.logistics.delivery_service.dtos.responseDTOs.LivreurDTO;
import com.logistics.delivery_service.exceptions.NoAvailableDriverException;
import com.logistics.delivery_service.model.Livraison;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final UserServiceClient userServiceClient;

    /**
     * Trouve le meilleur livreur disponible pour une livraison
     * Critères: distance, note, nombre de livraisons effectuées
     */
    public Long findBestDriver(Livraison livraison) {
        log.info("Finding best driver for delivery: {}", livraison.getIdLivraison());

        // Récupérer les livreurs disponibles
        List<LivreurDTO> availableDrivers = userServiceClient.getAvailableLivreurs();

        if (availableDrivers.isEmpty()) {
            throw new NoAvailableDriverException("No available drivers found");
        }

        // Filtrer et trier les livreurs
        LivreurDTO bestDriver = availableDrivers.stream()
                .filter(driver -> driver.getDisponibilite())
                .min(Comparator
                        .comparing((LivreurDTO d) -> calculateDistance(
                                d.getLatitudeActuelle(), d.getLongitudeActuelle(),
                                livraison.getLatitudeDestination(), livraison.getLongitudeDestination()
                        ))
                        .thenComparing(Comparator.comparing(LivreurDTO::getNoteMoyenne).reversed())
                        .thenComparing(LivreurDTO::getNombreLivraisonsEffectuees)
                )
                .orElseThrow(() -> new NoAvailableDriverException("No suitable driver found"));

        log.info("Best driver found: {} (ID: {})", bestDriver.getNom(), bestDriver.getIdUser());
        return bestDriver.getIdUser();
    }

    /**
     * Calcule la distance entre deux points GPS (formule de Haversine)
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE; // Si coordonnées manquantes, considérer très loin
        }

        final int R = 6371; // Rayon de la Terre en km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}