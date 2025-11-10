package com.logistics.delivery_service.dtos.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivreurDTO {
    private Long idUser;
    private String email;
    private String nom;
    private String telephone;
    private Boolean disponibilite;
    private Double latitudeActuelle;
    private Double longitudeActuelle;
    private Integer nombreLivraisonsEffectuees;
    private Double noteMoyenne;
}