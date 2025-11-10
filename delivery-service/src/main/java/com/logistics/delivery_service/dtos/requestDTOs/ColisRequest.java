package com.logistics.delivery_service.dtos.requestDTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColisRequest {

    @NotNull(message = "Package ID is required")
    private Long packageId;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double poids;

    private String description;

    private String dimensions; // Ex: "30x20x10"
}