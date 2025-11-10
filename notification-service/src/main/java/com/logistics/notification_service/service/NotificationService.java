package com.logistics.notification_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistics.notification_service.client.UserServiceClient;
import com.logistics.notification_service.dtos.UserDTO;
import com.logistics.notification_service.dtos.requestDTOs.NotificationRequest;
import com.logistics.notification_service.dtos.responseDTOs.NotificationResponse;
import com.logistics.notification_service.dtos.responseDTOs.NotificationStatsResponse;
import com.logistics.notification_service.enums.CategorieNotification;
import com.logistics.notification_service.enums.StatutNotification;
import com.logistics.notification_service.enums.TypeNotification;
import com.logistics.notification_service.exception.NotificationNotFoundException;
import com.logistics.notification_service.model.Notification;
import com.logistics.notification_service.repository.NotificationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SMSService smsService;
    private final PushNotificationService pushNotificationService;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Transactional
    @Async
    public void sendNotification(NotificationRequest request) {
        log.info("Sending notification to user: {}", request.getUserId());

        try {
            // Récupérer les informations de l'utilisateur
            UserDTO user = userServiceClient.getUserById(request.getUserId());
            log.info("this is the user info", user);
            // Créer la notification
            Notification notification = new Notification();
            notification.setUserId(request.getUserId());
            notification.setType(request.getType());
            notification.setCategorie(request.getCategorie() != null ? request.getCategorie() : CategorieNotification.SYSTEME);
            notification.setStatut(StatutNotification.EN_ATTENTE);
            notification.setSujet(request.getSubject());
            notification.setMessage(request.getMessage());
            notification.setTemplateName(request.getTemplateName());
            notification.setReferenceExterne(request.getReferenceExterne());
            notification.setPriorite(request.getPriorite() != null ? request.getPriorite() : 5);

            // Sérialiser les données du template
            if (request.getTemplateData() != null) {
                try {
                    notification.setTemplateData(objectMapper.writeValueAsString(request.getTemplateData()));
                } catch (JsonProcessingException e) {
                    log.error("Error serializing template data", e);
                }
            }

            // Définir le destinataire selon le type
            String destinataire = getDestinataire(user, request.getType());
            notification.setDestinataire(destinataire);

            // Sauvegarder
            Notification savedNotification = notificationRepository.save(notification);

            // Envoyer la notification
            boolean success = sendNotificationByType(savedNotification, user, request.getTemplateData());

            // Mettre à jour le statut
            if (success) {
                savedNotification.setStatut(StatutNotification.ENVOYEE);
                savedNotification.setDateEnvoi(LocalDateTime.now());
            } else {
                savedNotification.setStatut(StatutNotification.ECHOUEE);
                savedNotification.setNombreTentatives(savedNotification.getNombreTentatives() + 1);
            }

            notificationRepository.save(savedNotification);
            log.info("Notification sent successfully");

        } catch (FeignException e) {
            log.error("Failed to fetch user information", e);
        } catch (Exception e) {
            log.error("Error sending notification", e);
        }
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        log.info("Fetching notification with ID: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + id));

        return mapToNotificationResponse(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        log.info("Fetching unread notifications for user: {}", userId);
        return notificationRepository.findUnreadNotifications(userId).stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));

        notification.setStatut(StatutNotification.LUE);
        notification.setDateLecture(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        List<Notification> unreadNotifications = notificationRepository.findUnreadNotifications(userId);

        unreadNotifications.forEach(notification -> {
            notification.setStatut(StatutNotification.LUE);
            notification.setDateLecture(LocalDateTime.now());
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional(readOnly = true)
    public NotificationStatsResponse getNotificationStats() {
        log.info("Fetching notification statistics");

        Long total = notificationRepository.count();
        Long envoyees = notificationRepository.countByStatut(StatutNotification.ENVOYEE);
        Long echouees = notificationRepository.countByStatut(StatutNotification.ECHOUEE);
        Long enAttente = notificationRepository.countByStatut(StatutNotification.EN_ATTENTE);
        Long lues = notificationRepository.countByStatut(StatutNotification.LUE);

        Double tauxReussite = total > 0 ? (envoyees * 100.0) / total : 0.0;
        Double tauxLecture = envoyees > 0 ? (lues * 100.0) / envoyees : 0.0;

        return NotificationStatsResponse.builder()
                .totalNotifications(total)
                .notificationsEnvoyees(envoyees)
                .notificationsEchouees(echouees)
                .notificationsEnAttente(enAttente)
                .notificationsLues(lues)
                .tauxReussite(tauxReussite)
                .tauxLecture(tauxLecture)
                .build();
    }

    @Transactional
    public void retryFailedNotifications() {
        log.info("Retrying failed notifications");
        List<Notification> failedNotifications = notificationRepository.findPendingNotifications(StatutNotification.ECHOUEE);

        failedNotifications.forEach(notification -> {
            try {
                UserDTO user = userServiceClient.getUserById(notification.getUserId());

                Map<String, Object> templateData = null;
                if (notification.getTemplateData() != null) {
                    try {
                        templateData = objectMapper.readValue(notification.getTemplateData(), Map.class);
                    } catch (JsonProcessingException e) {
                        log.error("Error deserializing template data", e);
                    }
                }

                boolean success = sendNotificationByType(notification, user, templateData);

                if (success) {
                    notification.setStatut(StatutNotification.ENVOYEE);
                    notification.setDateEnvoi(LocalDateTime.now());
                } else {
                    notification.setNombreTentatives(notification.getNombreTentatives() + 1);
                }

                notificationRepository.save(notification);
            } catch (Exception e) {
                log.error("Error retrying notification", e);
            }
        });
    }

    private boolean sendNotificationByType(Notification notification, UserDTO user, Map<String, Object> templateData) {
        try {
            switch (notification.getType()) {
                case EMAIL:
                    if (notification.getTemplateName() != null && templateData != null) {
                        return emailService.sendTemplateEmail(
                                user.getEmail(),
                                notification.getSujet(),
                                notification.getTemplateName(),
                                templateData
                        );
                    } else {
                        return emailService.sendSimpleEmail(
                                user.getEmail(),
                                notification.getSujet(),
                                notification.getMessage()
                        );
                    }

                case SMS:
                    return smsService.sendSMS(user.getTelephone(), notification.getMessage());

                case PUSH:
                    return pushNotificationService.sendPushNotification(
                            notification.getUserId(),
                            notification.getSujet(),
                            notification.getMessage()
                    );

                default:
                    log.warn("Unsupported notification type: {}", notification.getType());
                    return false;
            }
        } catch (Exception e) {
            log.error("Error sending notification", e);
            return false;
        }
    }

    private String getDestinataire(UserDTO user, TypeNotification type) {
        return switch (type) {
            case EMAIL -> user.getEmail();
            case SMS -> user.getTelephone();
            case PUSH, IN_APP -> user.getIdUser().toString();
        };
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .idNotification(notification.getIdNotification())
                .userId(notification.getUserId())
                .type(notification.getType())
                .categorie(notification.getCategorie())
                .statut(notification.getStatut())
                .destinataire(notification.getDestinataire())
                .sujet(notification.getSujet())
                .message(notification.getMessage())
                .dateEnvoi(notification.getDateEnvoi())
                .dateLecture(notification.getDateLecture())
                .nombreTentatives(notification.getNombreTentatives())
                .erreurMessage(notification.getErreurMessage())
                .referenceExterne(notification.getReferenceExterne())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}