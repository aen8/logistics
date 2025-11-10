package com.logistics.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "livreurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "id_user") // ✅ Shared PK with User

public class Livreur extends User {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private Boolean disponibilite = true;

    private Long vehiculeId; // ID du véhicule dans fleet-service

    @Column(name = "latitude_actuelle")
    private Double latitudeActuelle;

    @Column(name = "longitude_actuelle")
    private Double longitudeActuelle;

    @Column(name = "nombre_livraisons_effectuees")
    private Integer nombreLivraisonsEffectuees = 0;

    @Column(name = "note_moyenne")
    private Double noteMoyenne = 0.0;
}