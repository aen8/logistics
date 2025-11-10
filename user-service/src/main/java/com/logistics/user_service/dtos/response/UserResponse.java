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
public class UserResponse {
    private Long idUser;
    private String email;
    private String role;
    private String nom;
    private String telephone;
    private Boolean active;
    private LocalDateTime createdAt;
}