package com.logistics.delivery_service.dtos;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdresseDTO {
    private String rue;
    private String ville;
    private String codePostal;
    private String pays ;
    private Double latitude;
    private Double longitude;
}