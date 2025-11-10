package com.logistics.warehouse_service.model;

import com.logistics.warehouse_service.enums.StatutEntrepot;
import com.logistics.warehouse_service.enums.TypeEntrepot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entrepots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrepot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEntrepot;

    @Column(nullable = false, unique = true)
    private String code; // Ex: WH-CAS-001

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEntrepot type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEntrepot statut;

    // Adresse de l'entrepôt
    @Column(nullable = false)
    private String adresseRue;

    @Column(nullable = false)
    private String adresseVille;

    @Column(nullable = false)
    private String adresseCodePostal;

    private String adressePays = "Maroc";

    private Double latitude;

    private Double longitude;

    @Column(name = "capacite_max", nullable = false)
    private Integer capaciteMax; // En nombre de colis

    @Column(name = "capacite_actuelle", nullable = false)
    private Integer capaciteActuelle = 0;

    @Column(name = "superficie_m2")
    private Double superficieM2;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email")
    private String email;

    @Column(name = "responsable_nom")
    private String responsableNom;

    @Column(name = "responsable_telephone")
    private String responsableTelephone;

    @Column(name = "horaires_ouverture")
    private String horairesOuverture; // Ex: "Lun-Ven: 8h-18h"

    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "entrepot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Stock> stocks = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (statut == null) {
            statut = StatutEntrepot.ACTIF;
        }
        if (capaciteActuelle == null) {
            capaciteActuelle = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calcule le taux d'occupation de l'entrepôt
     */
    public Double getTauxOccupation() {
        if (capaciteMax == null || capaciteMax == 0) {
            return 0.0;
        }
        return (capaciteActuelle * 100.0) / capaciteMax;
    }

    /**
     * Calcule la capacité disponible
     */
    public Integer getCapaciteDisponible() {
        return capaciteMax - capaciteActuelle;
    }

    /**
     * Vérifie si l'entrepôt peut accueillir un nombre de colis
     */
    public boolean hasCapacityFor(int nombreColis) {
        return getCapaciteDisponible() >= nombreColis;
    }
}