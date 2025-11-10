package com.logistics.warehouse_service.dtos.responseDTOs;

import com.logistics.warehouse_service.dtos.AdresseDTO;
import com.logistics.warehouse_service.enums.StatutEntrepot;
import com.logistics.warehouse_service.enums.TypeEntrepot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntrepotResponse {
    private Long idEntrepot;
    private String code;
    private String nom;
    private TypeEntrepot type;
    private StatutEntrepot statut;
    private AdresseDTO adresse;
    private Integer capaciteMax;
    private Integer capaciteActuelle;
    private Integer capaciteDisponible;
    private Double tauxOccupation;
    private Double superficieM2;
    private String telephone;
    private String email;
    private String responsableNom;
    private String responsableTelephone;
    private String horairesOuverture;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}