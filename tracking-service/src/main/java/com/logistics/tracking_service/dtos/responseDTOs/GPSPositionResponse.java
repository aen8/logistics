package com.logistics.tracking_service.dtos.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPSPositionResponse {

    private Long idPosition;
    private Long livreurId;
    private Long livraisonId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dateHeure;
    private Double vitesse;
    private Double altitude;
    private Double precision;
    private String adresse;
    private Integer batteryLevel;
    private Boolean isMoving;
    private LocalDateTime createdAt;
}