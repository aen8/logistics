package com.logistics.route_service.dtos.responseDTOs;


import com.logistics.route_service.enums.TypeRoute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtapeResponse {
    private Long idEtape;
    private Integer ordre;
    private Long location;
    private String nomLieu;
    private String description;
    private TypeRoute typeRoute;
    private Double distanceDepuisPrecedent;
    private Integer dureeDepuisPrecedent;
    private String instructions;
}