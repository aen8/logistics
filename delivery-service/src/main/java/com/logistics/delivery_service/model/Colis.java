package com.logistics.delivery_service.model;

import com.logistics.delivery_service.enums.EtatColis;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "colis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idColis;

    @Column(name = "package_id", nullable = false)
    private Long packageId; // ID du colis dans order-service

    @Column(nullable = false)
    private Double poids;

    @Column(length = 500)
    private String description;

    @Column(name = "dimensions")
    private String dimensions; // Ex: "30x20x10 cm"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatColis etat;

    @Column(name = "code_tracking", unique = true)
    private String codeTracking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livraison_id")
    private Livraison livraison;

    @Column(name = "date_scan")
    private LocalDateTime dateScan;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (etat == null) {
            etat = EtatColis.EN_ATTENTE;
        }
        if (codeTracking == null) {
            codeTracking = generateTrackingCode();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateTrackingCode() {
        return "TRK" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
}