package com.logistics.user_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerDashboardResponse {
    private Long managerId;
    private String managerName;
    private String region;
    private Integer teamSize;
    private Long totalLivraisonsInRegion;
    private Long pendingLivraisons;
    private Long completedLivraisons;
    private Long availableLivreurs;
    private Double averageDeliveryTime;
}