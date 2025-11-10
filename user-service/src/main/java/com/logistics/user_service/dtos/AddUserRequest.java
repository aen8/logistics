package com.logistics.user_service.dtos;

import com.logistics.user_service.enums.UserRole;
import lombok.Data;

@Data
public class AddUserRequest {
    private String email;
    private String password;
    private String nom;
    private String telephone;
    private UserRole role;
    private AdresseRequest adresse;
}
