package com.logistics.warehouse_service.dtos.requestDTOs;

import com.logistics.warehouse_service.enums.StatutEntrepot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEntrepotRequest {
    private String nom;
    private StatutEntrepot statut;
    private Integer capaciteMax;
    private String telephone;
    private String email;
    private String responsableNom;
    private String responsableTelephone;
    private String horairesOuverture;
    private String description;
}