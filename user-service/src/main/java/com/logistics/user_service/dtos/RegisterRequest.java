package com.logistics.user_service.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

//    @NotBlank(message = "Role is required")
//    @Pattern(regexp = "CLIENT|LIVREUR|ADMIN|MANAGER", message = "Role must be CLIENT, LIVREUR, ADMIN, or MANAGER")
//    private String role;

    @NotBlank(message = "Name is required")
    private String nom;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String telephone;

    // Champs optionnels pour Client
//    private AdresseRequest adresse;
}