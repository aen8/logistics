package com.logistics.delivery_service.dtos.responseDTOs;

import com.logistics.delivery_service.enums.EtatColis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColisResponse {
    private Long idColis;
    private Long packageId;
    private Double poids;
    private String description;
    private String dimensions;
    private EtatColis etat;
    private String codeTracking;
    private LocalDateTime dateScan;
    private String notes;
    private LocalDateTime createdAt;
}