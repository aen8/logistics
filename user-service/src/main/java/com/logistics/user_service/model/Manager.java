package com.logistics.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "managers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id_user") // ✅ Shared PK with User

public class Manager extends User {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String telephone;

    private String region; // Région de responsabilité

    @Column(name = "equipe_taille")
    private Integer equipeTaille = 0;
}