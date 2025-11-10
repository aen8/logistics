package com.logistics.route_service.dtos.requestDTOs;

import com.logistics.route_service.dtos.LocationDTO;
import com.logistics.route_service.enums.TypeCalcul;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteCalculationRequest {

    @NotNull(message = "Origin is required")
    private LocationDTO origine;

    @NotNull(message = "Destination is required")
    private LocationDTO destination;

    private List<LocationDTO> etapesIntermediaires; // Points de passage

    private TypeCalcul typeCalcul; // Type de calcul (par défaut: DISTANCE_COURTE)

    private Long livraisonId; // ID de la livraison associée

    private String vehiculeType; // Type de véhicule (pour le calcul de carburant)
}