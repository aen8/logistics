package com.logistics.user_service.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdresseRequest {

    @NotBlank(message = "Street is required")
    private String rue;

    @NotBlank(message = "City is required")
    private String ville;

    @NotBlank(message = "Postal code is required")
    private String codePostal;

    private String pays = "Maroc";

    private Double latitude;

    private Double longitude;

    private Boolean estPrincipale = false;
}