package com.logistics.warehouse_service.dtos.requestDTOs;

import com.logistics.warehouse_service.enums.TypeStock;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest {

    @NotNull(message = "Warehouse ID is required")
    private Long entrepotId;

    @NotNull(message = "Package ID is required")
    private Long colisId;

    @NotNull(message = "Stock type is required")
    private TypeStock type;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantite;

    private String zoneStockage;

    private String referenceDocument;

    private Double poidsTotal;

    private String notes;

    private String effectuePar;
}