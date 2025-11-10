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
public class ManagerResponse {
    private Long idUser;
    private String email;
    private String nom;
    private String telephone;
    private String region;
    private Integer equipeTaille;
    private Boolean active;
    private LocalDateTime createdAt;
}