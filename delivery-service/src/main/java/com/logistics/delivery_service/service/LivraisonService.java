package com.logistics.delivery_service.service;

import com.logistics.delivery_service.client.NotificationServiceClient;
import com.logistics.delivery_service.client.RouteServiceClient;
import com.logistics.delivery_service.client.UserServiceClient;
import com.logistics.delivery_service.client.WarehouseServiceClient;
import com.logistics.delivery_service.dtos.AdresseDTO;
import com.logistics.delivery_service.dtos.requestDTOs.*;
import com.logistics.delivery_service.dtos.responseDTOs.ColisResponse;
import com.logistics.delivery_service.dtos.responseDTOs.LivraisonResponse;
import com.logistics.delivery_service.dtos.responseDTOs.LivreurDTO;
import com.logistics.delivery_service.dtos.responseDTOs.RouteCalculationResponse;
import com.logistics.delivery_service.enums.EtatColis;
import com.logistics.delivery_service.enums.EtatLivraison;
import com.logistics.delivery_service.enums.TypeLivraison;
import com.logistics.delivery_service.exceptions.LivraisonNotFoundException;
import com.logistics.delivery_service.model.Colis;
import com.logistics.delivery_service.model.Livraison;
import com.logistics.delivery_service.repository.LivraisonRepository;
import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivraisonService {
    private final LivraisonRepository livraisonRepository;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    private final RouteServiceClient routeServiceClient;
    private final WarehouseServiceClient warehouseServiceClient;
    private  final  AssignmentService assignmentService ;
    private AdresseDTO destination;//Solution temporaire

    @Transactional
    public LivraisonResponse createLivraison(LivraisonRequest request){
        log.info("Creating delivery for order: {}", request.getOrderId());
        //Create livraison
        Livraison livraison = new Livraison() ;
        livraison.setOrderId(request.getOrderId());
        livraison.setClientId(request.getClientId());
        livraison.setEtat(EtatLivraison.EN_ATTENTE);
        livraison.setType(request.getType());
        livraison.setNotes(request.getNotes());
        livraison.setAdresseDestinationCodePostal(request.getAdresseDestination().getCodePostal());

        // Adresse d'origine (entrepôt)
        if (request.getAdresseOrigine() != null) {
            AdresseDTO origine = request.getAdresseOrigine();
            livraison.setAdresseOrigineRue(origine.getRue());
            livraison.setAdresseOrigineVille(origine.getVille());
            livraison.setAdresseOrigineCodePostal(origine.getCodePostal());
            livraison.setLatitudeOrigine(origine.getLatitude());
            livraison.setLongitudeOrigine(origine.getLongitude());
            livraison.setAdresseDestinationRue(request.getAdresseDestination().getRue());
            livraison.setAdresseDestinationVille(request.getAdresseDestination().getVille());
            livraison.setAdresseDestinationCodePostal(request.getAdresseDestination().getCodePostal());
        }

        // Calculer la distance et le prix
//        try {
//            RouteCalculationRequest routeRequest = RouteCalculationRequest.builder()
//                    .origine(request.getAdresseOrigine() != null ? request.getAdresseOrigine() : getDefaultWarehouseAddress())
//                    .destination(destination)
//                    .build();
//            RouteCalculationResponse routeResponse = routeServiceClient.calculateRoute(routeRequest);
//            livraison.setDistanceKm(routeResponse.getDistanceKm());
//            livraison.setPrixLivraison(routeResponse.getCoutEstime());
//        } catch (FeignException e) {
//            log.warn("Failed to calculate route, using default values", e);
//            livraison.setDistanceKm(0.0);
//            livraison.setPrixLivraison(calculateDefaultPrice(request.getType()));
//        }

        // Date de livraison prévue
        livraison.setDateLivraisonPrevue(
                request.getDateLivraisonPrevue() != null
                        ? request.getDateLivraisonPrevue()
                        : calculateExpectedDeliveryDate(request.getType())
        );

        // Ajouter les colis
        for (ColisRequest colisReq : request.getColis()) {
            Colis colis = new Colis();
            colis.setPackageId(colisReq.getPackageId());
            colis.setPoids(colisReq.getPoids());
            colis.setDescription(colisReq.getDescription());
            colis.setDimensions(colisReq.getDimensions());
            colis.setEtat(EtatColis.EN_ATTENTE);
            livraison.addColis(colis);
        }

        // Sauvegarder
        Livraison savedLivraison = livraisonRepository.save(livraison);
        log.info("Delivery created with ID: {}", savedLivraison.getIdLivraison());

        // Envoyer notification au client
        sendNotificationToClient(savedLivraison);

        return mapToLivraisonResponse(savedLivraison);
    }



    @Transactional(readOnly = true)
    public LivraisonResponse getLivraisonById(Long id) {
        log.info("Fetching delivery with ID: {}", id);
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new LivraisonNotFoundException("Delivery not found with ID: " + id));

        return mapToLivraisonResponse(livraison);
    }

    @Transactional(readOnly = true)
    public List<LivraisonResponse> getAllLivraisons() {
        log.info("Fetching all deliveries");
        return livraisonRepository.findAll().stream()
                .map(this::mapToLivraisonResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LivraisonResponse> getLivraisonsByClient(Long clientId) {
        log.info("Fetching deliveries for client: {}", clientId);
        return livraisonRepository.findByClientId(clientId).stream()
                .map(this::mapToLivraisonResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LivraisonResponse> getLivraisonsByLivreur(Long livreurId) {
        log.info("Fetching deliveries for driver: {}", livreurId);
        return livraisonRepository.findByLivreurId(livreurId).stream()
                .map(this::mapToLivraisonResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LivraisonResponse> getLivraisonsByStatus(EtatLivraison etat) {
        log.info("Fetching deliveries with status: {}", etat);
        return livraisonRepository.findByEtat(etat).stream()
                .map(this::mapToLivraisonResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LivraisonResponse assignDriver(AssignDriverRequest request) {
        log.info("Assigning driver to delivery: {}", request.getLivraisonId());

        Livraison livraison = livraisonRepository.findById(request.getLivraisonId())
                .orElseThrow(() -> new LivraisonNotFoundException("Delivery not found"));

        Long livreurId = request.getLivreurId();

        // Assignation automatique si aucun livreur spécifié
        if (livreurId == null) {
            livreurId = assignmentService.findBestDriver(livraison);
        }

        // Mettre à jour la livraison
        livraison.setLivreurId(livreurId);
        livraison.setEtat(EtatLivraison.ASSIGNEE);

        // Mettre à jour la disponibilité du livreur
        try {
            userServiceClient.updateLivreurDisponibilite(livreurId, false);
        } catch (FeignException e) {
            log.warn("Failed to update driver availability", e);
        }

        Livraison updatedLivraison = livraisonRepository.save(livraison);
        log.info("Driver {} assigned to delivery {}", livreurId, livraison.getIdLivraison());

        // Notifications
        sendNotificationToDriver(updatedLivraison);
        sendNotificationToClient(updatedLivraison);

        return mapToLivraisonResponse(updatedLivraison);
    }

    @Transactional
    public LivraisonResponse updateStatus(Long id, UpdateLivraisonStatusRequest request) {
        log.info("Updating delivery {} status to {}", id, request.getEtat());

        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new LivraisonNotFoundException("Delivery not found with ID: " + id));

        EtatLivraison oldStatus = livraison.getEtat();
        livraison.setEtat(request.getEtat());

        if (request.getNotes() != null) {
            livraison.setNotes(request.getNotes());
        }

        // Si livraison terminée
        if (request.getEtat() == EtatLivraison.LIVREE) {
            livraison.setDateLivraisonEffective(LocalDateTime.now());

            // Mettre à jour l'état des colis
            livraison.getColisList().forEach(colis -> colis.setEtat(EtatColis.LIVRE));

            // Libérer le livreur
            if (livraison.getLivreurId() != null) {
                try {
                    userServiceClient.updateLivreurDisponibilite(livraison.getLivreurId(), true);
                } catch (FeignException e) {
                    log.warn("Failed to update driver availability", e);
                }
            }
        }

        Livraison updatedLivraison = livraisonRepository.save(livraison);
        log.info("Delivery status updated from {} to {}", oldStatus, request.getEtat());

        // Notification
        sendStatusUpdateNotification(updatedLivraison);

        return mapToLivraisonResponse(updatedLivraison);
    }

    @Transactional
    public void cancelLivraison(Long id, String reason) {
        log.info("Cancelling delivery: {}", id);

        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new LivraisonNotFoundException("Delivery not found with ID: " + id));

        livraison.setEtat(EtatLivraison.ANNULEE);
        livraison.setNotes("Annulée: " + reason);

        // Libérer le livreur si assigné
        if (livraison.getLivreurId() != null) {
            try {
                userServiceClient.updateLivreurDisponibilite(livraison.getLivreurId(), true);
            } catch (FeignException e) {
                log.warn("Failed to update driver availability", e);
            }
        }

        livraisonRepository.save(livraison);

        // Notification
        sendCancellationNotification(livraison, reason);
    }

    @Transactional(readOnly = true)
    public List<LivraisonResponse> getActiveLivraisons() {
        log.info("Fetching active deliveries");
        List<EtatLivraison> activeStatuses = Arrays.asList(
                EtatLivraison.EN_ATTENTE,
                EtatLivraison.ASSIGNEE,
                EtatLivraison.EN_PREPARATION,
                EtatLivraison.EN_COURS
        );

        return livraisonRepository.findByEtatIn(activeStatuses).stream()
                .map(this::mapToLivraisonResponse)
                .collect(Collectors.toList());
    }

// Méthodes privées

    private LivraisonResponse mapToLivraisonResponse(Livraison livraison) {
        String livreurNom = null;
        if (livraison.getLivreurId() != null) {
            try {
                LivreurDTO livreur = userServiceClient.getLivreurById(livraison.getLivreurId());
                livreurNom = livreur.getNom();
            } catch (FeignException e) {
                log.warn("Failed to fetch driver name", e);
            }
        }

        List<ColisResponse> colisResponses = livraison.getColisList().stream()
                .map(this::mapToColisResponse)
                .collect(Collectors.toList());

        AdresseDTO origine = AdresseDTO.builder()
                .rue(livraison.getAdresseOrigineRue())
                .ville(livraison.getAdresseOrigineVille())
                .codePostal(livraison.getAdresseOrigineCodePostal())
                .latitude(livraison.getLatitudeOrigine())
                .longitude(livraison.getLongitudeOrigine())
                .build();

        AdresseDTO destination = AdresseDTO.builder()
                .rue(livraison.getAdresseDestinationRue())
                .ville(livraison.getAdresseDestinationVille())
                .codePostal(livraison.getAdresseDestinationCodePostal())
                .latitude(livraison.getLatitudeDestination())
                .longitude(livraison.getLongitudeDestination())
                .build();

        return LivraisonResponse.builder()
                .idLivraison(livraison.getIdLivraison())
                .orderId(livraison.getOrderId())
                .clientId(livraison.getClientId())
                .livreurId(livraison.getLivreurId())
                .livreurNom(livreurNom)
                .dateCreation(livraison.getDateCreation())
                .dateLivraisonPrevue(livraison.getDateLivraisonPrevue())
                .dateLivraisonEffective(livraison.getDateLivraisonEffective())
                .etat(livraison.getEtat())
                .type(livraison.getType())
                .distanceKm(livraison.getDistanceKm())
                .adresseOrigine(origine)
                .adresseDestination(destination)
                .notes(livraison.getNotes())
                .prixLivraison(livraison.getPrixLivraison())
                .colis(colisResponses)
                .createdAt(livraison.getCreatedAt())
                .updatedAt(livraison.getUpdatedAt())
                .build();
    }

    private ColisResponse mapToColisResponse(Colis colis) {
        return ColisResponse.builder()
                .idColis(colis.getIdColis())
                .packageId(colis.getPackageId())
                .poids(colis.getPoids())
                .description(colis.getDescription())
                .dimensions(colis.getDimensions())
                .etat(colis.getEtat())
                .codeTracking(colis.getCodeTracking())
                .dateScan(colis.getDateScan())
                .notes(colis.getNotes())
                .createdAt(colis.getCreatedAt())
                .build();
    }

    private LocalDateTime calculateExpectedDeliveryDate(TypeLivraison type) {
        LocalDateTime now = LocalDateTime.now();
        return switch (type) {
            case SAME_DAY -> now.plusHours(6);
            case EXPRESS -> now.plusDays(1);
            case STANDARD -> now.plusDays(3);
            case PLANIFIEE -> now.plusDays(7);
        };
    }

    private Double calculateDefaultPrice(TypeLivraison type) {
        return switch (type) {
            case SAME_DAY -> 100.0;
            case EXPRESS -> 50.0;
            case STANDARD -> 25.0;
            case PLANIFIEE -> 30.0;
        };
    }

    private AdresseDTO getDefaultWarehouseAddress() {
        return AdresseDTO.builder()
                .rue("Zone Industrielle")
                .ville("Casablanca")
                .codePostal("20000")
                .pays("Maroc")
                .latitude(33.5731)
                .longitude(-7.5898)
                .build();
    }

    private void sendNotificationToClient(Livraison livraison) {
        try {
            NotificationRequest notification = NotificationRequest.builder()
                    .userId(livraison.getClientId())
                    .type("EMAIL")
                    .subject("Mise à jour de votre livraison")
                    .message(String.format("Votre livraison #%d a été créée. Statut: %s",
                            livraison.getIdLivraison(), livraison.getEtat()))
                    .build();
            notificationServiceClient.sendNotification(notification);
        } catch (FeignException e) {
            log.warn("Failed to send notification to client", e);
        }
    }

    private void sendNotificationToDriver(Livraison livraison) {
        try {
            NotificationRequest notification = NotificationRequest.builder()
                    .userId(livraison.getLivreurId())
                    .type("PUSH")
                    .subject("Nouvelle livraison assignée")
                    .message(String.format("Livraison #%d vous a été assignée", livraison.getIdLivraison()))
                    .build();
            notificationServiceClient.sendNotification(notification);
        } catch (FeignException e) {
            log.warn("Failed to send notification to driver", e);
        }
    }

    private void sendStatusUpdateNotification(Livraison livraison) {
        try {
            NotificationRequest notification = NotificationRequest.builder()
                    .userId(livraison.getClientId())
                    .type("PUSH")
                    .subject("Statut de livraison mis à jour")
                    .message(String.format("Votre livraison #%d: %s",
                            livraison.getIdLivraison(), livraison.getEtat()))
                    .build();
            notificationServiceClient.sendNotification(notification);
        } catch (FeignException e) {
            log.warn("Failed to send status update notification", e);
        }
    }

    private void sendCancellationNotification(Livraison livraison, String reason) {
        try {
            NotificationRequest notification = NotificationRequest.builder()
                    .userId(livraison.getClientId())
                    .type("EMAIL")
                    .subject("Livraison annulée")
                    .message(String.format("Votre livraison #%d a été annulée. Raison: %s",
                            livraison.getIdLivraison(), reason))
                    .build();
            notificationServiceClient.sendNotification(notification);
        } catch (FeignException e) {
            log.warn("Failed to send cancellation notification", e);
        }
    }


}
