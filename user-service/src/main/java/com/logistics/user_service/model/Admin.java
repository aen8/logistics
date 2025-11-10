package com.logistics.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String telephone;

    private String departement;

    @Column(name = "niveau_acces")
    private String niveauAcces = "FULL"; // FULL, LIMITED
}