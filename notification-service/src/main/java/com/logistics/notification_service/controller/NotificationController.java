package com.logistics.notification_service.controller;

import com.logistics.notification_service.dtos.requestDTOs.EmailRequest;
import com.logistics.notification_service.dtos.requestDTOs.NotificationRequest;
import com.logistics.notification_service.dtos.requestDTOs.SMSRequest;
import com.logistics.notification_service.dtos.responseDTOs.NotificationResponse;
import com.logistics.notification_service.dtos.responseDTOs.NotificationStatsResponse;
import com.logistics.notification_service.service.EmailService;
import com.logistics.notification_service.service.NotificationService;
import com.logistics.notification_service.service.SMSService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SMSService smsService;

    /**
     * Envoyer une notification (utilisé par les autres microservices)
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody NotificationRequest request) {
        notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Notification queued for sending");
    }

    /**
     * Envoyer un email direct
     */
    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest request) {
        boolean success = emailService.sendSimpleEmail(request.getTo(), request.getSubject(), request.getMessage());
        if (success) {
            return ResponseEntity.ok("Email sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }

    /**
     * Envoyer un SMS direct
     */
    @PostMapping("/sms")
    public ResponseEntity<String> sendSMS(@Valid @RequestBody SMSRequest request) {
        boolean success = smsService.sendSMS(request.getPhoneNumber(), request.getMessage());
        if (success) {
            return ResponseEntity.ok("SMS sent successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send SMS");
        }
    }

    /**
     * Récupérer une notification par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer toutes les notifications d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUser(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@PathVariable Long userId) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Compter les notifications non lues
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@PathVariable Long userId) {
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Marquer une notification comme lue
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtenir les statistiques des notifications
     */
    @GetMapping("/stats")
    public ResponseEntity<NotificationStatsResponse> getNotificationStats() {
        NotificationStatsResponse stats = notificationService.getNotificationStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Réessayer les notifications échouées
     */
    @PostMapping("/retry-failed")
    public ResponseEntity<String> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok("Retrying failed notifications");
    }
}