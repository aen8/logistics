package com.logistics.delivery_service.dtos.requestDTOs;

import com.logistics.delivery_service.dtos.AdresseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCalculationRequest {
    private AdresseDTO origine;
    private AdresseDTO destination;
}