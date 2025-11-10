package com.logistics.warehouse_service.dtos.requestDTOs;

import com.logistics.warehouse_service.dtos.AdresseDTO;
import com.logistics.warehouse_service.enums.TypeEntrepot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrepotRequest {

    @NotBlank(message = "Warehouse code is required")
    private String code;

    @NotBlank(message = "Warehouse name is required")
    private String nom;

    @NotNull(message = "Warehouse type is required")
    private TypeEntrepot type;

    @NotNull(message = "Address is required")
    private AdresseDTO adresse;

    @NotNull(message = "Max capacity is required")
    @Positive(message = "Max capacity must be positive")
    private Integer capaciteMax;

    private Double superficieM2;

    private String telephone;

    private String email;

    private String responsableNom;

    private String responsableTelephone;

    private String horairesOuverture;

    private String description;
}