package com.logistics.delivery_service.dtos.requestDTOs;


import com.logistics.delivery_service.enums.EtatLivraison;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLivraisonStatusRequest {

    @NotNull(message = "Status is required")
    private EtatLivraison etat;

    private String notes;
}