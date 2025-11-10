package com.logistics.warehouse_service.model;

import com.logistics.warehouse_service.enums.TypeStock;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrepot_id", nullable = false)
    private Entrepot entrepot;

    @Column(name = "colis_id", nullable = false)
    private Long colisId; // ID du colis dans delivery-service

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeStock type;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;

    @Column(name = "zone_stockage")
    private String zoneStockage; // Ex: "Zone A - Allée 3 - Niveau 2"

    @Column(name = "reference_document")
    private String referenceDocument; // Référence bon de livraison, etc.

    @Column(name = "poids_total")
    private Double poidsTotal;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "effectue_par")
    private String effectuePar; // Nom de l'opérateur

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (dateMouvement == null) {
            dateMouvement = LocalDateTime.now();
        }
    }
}