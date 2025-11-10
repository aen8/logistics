package com.logistics.notification_service.model;

import com.logistics.notification_service.enums.CategorieNotification;
import com.logistics.notification_service.enums.StatutNotification;
import com.logistics.notification_service.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotification;

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID de l'utilisateur destinataire

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieNotification categorie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutNotification statut;

    @Column(nullable = false)
    private String destinataire; // Email, numéro de téléphone, ou device token

    @Column(nullable = false)
    private String sujet;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "template_name")
    private String templateName; // Nom du template utilisé

    @Column(name = "template_data", length = 5000)
    private String templateData; // Données JSON pour le template

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @Column(name = "nombre_tentatives")
    private Integer nombreTentatives = 0;

    @Column(name = "erreur_message", length = 1000)
    private String erreurMessage;

    @Column(name = "reference_externe")
    private String referenceExterne; // ID de commande, livraison, etc.

    @Column(name = "priorite")
    private Integer priorite = 5; // 1 = haute, 5 = normale, 10 = basse

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (statut == null) {
            statut = StatutNotification.EN_ATTENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}