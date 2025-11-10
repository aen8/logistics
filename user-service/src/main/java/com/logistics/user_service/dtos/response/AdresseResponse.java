package com.logistics.user_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdresseResponse {
    private Long idAdresse;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private Double latitude;
    private Double longitude;
    private Boolean estPrincipale;
}