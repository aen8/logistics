package com.logistics.user_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivreurResponse {
    private Long idUser;
    private String email;
    private String nom;
    private String telephone;
    private Boolean disponibilite;
    private Long vehiculeId;
    private Double latitudeActuelle;
    private Double longitudeActuelle;
    private Integer nombreLivraisonsEffectuees;
    private Double noteMoyenne;
    private Boolean active;
    private LocalDateTime createdAt;
}