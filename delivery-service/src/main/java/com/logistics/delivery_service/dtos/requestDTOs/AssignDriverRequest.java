package com.logistics.delivery_service.dtos.requestDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignDriverRequest {

    @NotNull(message = "Delivery ID is required")
    private Long livraisonId;

    private Long livreurId; // Si null, assignation automatique
}