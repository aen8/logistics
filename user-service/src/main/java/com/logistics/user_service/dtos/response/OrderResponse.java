package com.logistics.user_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long idCommande;
    private Long clientId;
    private LocalDateTime dateCommande;
    private Double prixTotal;
    private String statut;
    private Long deliveryId;
}