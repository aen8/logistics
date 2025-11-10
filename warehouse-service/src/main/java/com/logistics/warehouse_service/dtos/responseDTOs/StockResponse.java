package com.logistics.warehouse_service.dtos.responseDTOs;

import com.logistics.warehouse_service.enums.TypeStock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {
    private Long idStock;
    private Long entrepotId;
    private String entrepotNom;
    private Long colisId;
    private TypeStock type;
    private Integer quantite;
    private LocalDateTime dateMouvement;
    private String zoneStockage;
    private String referenceDocument;
    private Double poidsTotal;
    private String notes;
    private String effectuePar;
    private LocalDateTime createdAt;
}