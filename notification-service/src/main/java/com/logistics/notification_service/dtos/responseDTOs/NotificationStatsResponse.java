package com.logistics.notification_service.dtos.responseDTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationStatsResponse {
    private Long totalNotifications;
    private Long notificationsEnvoyees;
    private Long notificationsEchouees;
    private Long notificationsEnAttente;
    private Long notificationsLues;
    private Double tauxReussite;
    private Double tauxLecture;
}