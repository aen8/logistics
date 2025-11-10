package com.logistics.notification_service.dtos.requestDTOs;

import com.logistics.notification_service.enums.CategorieNotification;
import com.logistics.notification_service.enums.TypeNotification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Notification type is required")
    private TypeNotification type;

    private CategorieNotification categorie;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message is required")
    private String message;

    private String templateName;

    private Map<String, Object> templateData;

    private String referenceExterne;

    private Integer priorite;
}