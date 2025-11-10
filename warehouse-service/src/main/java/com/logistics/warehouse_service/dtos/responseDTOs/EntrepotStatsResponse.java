package com.logistics.warehouse_service.dtos.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntrepotStatsResponse {
    private Long entrepotId;
    private String entrepotNom;
    private Integer capaciteMax;
    private Integer capaciteActuelle;
    private Integer capaciteDisponible;
    private Double tauxOccupation;
    private Long nombreEntrees;
    private Long nombreSorties;
    private Long nombreTransferts;
}