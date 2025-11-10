package com.logistics.delivery_service.dtos.requestDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private Long userId;
    private String type; // EMAIL, SMS, PUSH
    private String subject;
    private String message;
}