package com.logistics.user_service.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {
    private Long idUser;
    private String email;
    private String nom;
    private String telephone;
    private Boolean active;
    private LocalDateTime createdAt;
    private List<AdresseResponse> adresses;
}