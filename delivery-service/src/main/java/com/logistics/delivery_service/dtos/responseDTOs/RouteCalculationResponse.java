package com.logistics.delivery_service.dtos.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCalculationResponse {
    private Double distanceKm;
    private Integer dureeMinutes;
    private Double coutEstime;
}