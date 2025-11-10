package com.logistics.route_service.dtos.responseDTOs;

import com.logistics.route_service.dtos.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptimizedRouteResponse {
    private List<LocationDTO> ordreOptimise;
    private Double distanceTotale;
    private Integer dureeTotale;
    private Double coutTotal;
    private String description;
}