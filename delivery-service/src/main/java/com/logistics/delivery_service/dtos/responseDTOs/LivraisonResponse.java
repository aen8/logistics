package com.logistics.delivery_service.dtos.responseDTOs;

import com.logistics.delivery_service.dtos.AdresseDTO;
import com.logistics.delivery_service.enums.EtatLivraison;
import com.logistics.delivery_service.enums.TypeLivraison;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivraisonResponse {
    private Long idLivraison;
    private Long orderId;
    private Long clientId;
    private Long livreurId;
    private String livreurNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateLivraisonPrevue;
    private LocalDateTime dateLivraisonEffective;
    private EtatLivraison etat;
    private TypeLivraison type;
    private Double distanceKm;
    private AdresseDTO adresseOrigine;
    private AdresseDTO adresseDestination;
    private String notes;
    private Double prixLivraison;
    private List<ColisResponse> colis;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}