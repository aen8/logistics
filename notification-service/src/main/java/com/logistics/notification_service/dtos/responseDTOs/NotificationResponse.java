package com.logistics.notification_service.dtos.responseDTOs;

import com.logistics.notification_service.enums.CategorieNotification;
import com.logistics.notification_service.enums.StatutNotification;
import com.logistics.notification_service.enums.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long idNotification;
    private Long userId;
    private TypeNotification type;
    private CategorieNotification categorie;
    private StatutNotification statut;
    private String destinataire;
    private String sujet;
    private String message;
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLecture;
    private Integer nombreTentatives;
    private String erreurMessage;
    private String referenceExterne;
    private LocalDateTime createdAt;
}