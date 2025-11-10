package com.logistics.tracking_service.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "gps_positions", indexes = {
        @Index(name = "idx_livreur_id", columnList = "livreur_id"),
        @Index(name = "idx_livraison_id", columnList = "livraison_id"),
        @Index(name = "idx_date_heure", columnList = "date_heure")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GPSPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPosition;

    @Column(name = "livreur_id", nullable = false)
    private Long livreurId;

    @Column(name = "livraison_id")
    private Long livraisonId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column
    private Double vitesse; // En km/h

    @Column
    private Double altitude; // En mètres

    @Column(name = "the_precision")
    private Double precision; // Précision GPS en mètres

    @Column
    private String adresse; // Adresse géocodée (optionnelle)

    @Column(name = "battery_level")
    private Integer batteryLevel; // Niveau de batterie du device

    @Column(name = "is_moving")
    private Boolean isMoving; // Le livreur est en mouvement

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateHeure == null) {
            dateHeure = LocalDateTime.now();
        }
        if (isMoving == null) {
            isMoving = false;
        }
    }
}