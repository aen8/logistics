package com.logistics.user_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private Long totalUsers;
    private Long totalClients;
    private Long totalLivreurs;
    private Long totalManagers;
    private Long totalAdmins;
    private Long activeUsers;
    private Long availableLivreurs;
    private Map<String,Long> monthlyUsers;
}