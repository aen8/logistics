package com.logistics.delivery_service.dtos.requestDTOs;

import com.logistics.delivery_service.dtos.AdresseDTO;
import com.logistics.delivery_service.enums.TypeLivraison;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivraisonRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Delivery address is required")
    private AdresseDTO adresseDestination;

    private AdresseDTO adresseOrigine; // Si null, utilisera l'entrepôt par défaut

    @NotNull(message = "Delivery type is required")
    private TypeLivraison type;

    @NotEmpty(message = "At least one package is required")
    private List<ColisRequest> colis;

    private LocalDateTime dateLivraisonPrevue;

    private String notes;
}