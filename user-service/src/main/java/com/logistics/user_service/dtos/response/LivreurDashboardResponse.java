package com.logistics.user_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivreurDashboardResponse {

    private DashboardStats stats;
    private List<DeliveryInfo> todaysDeliveries;
    private RouteInfo routeInfo;
    private List<NotificationInfo> notifications;

    // ---------------- Nested DTOs ---------------- //

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStats {
        private long totalDeliveries;
        private long pending;
        private long completed;
        private long delayed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryInfo {
        private Long orderId;
        private String customerName;
        private String customerPhone;
        private String pickupLocation;
        private String deliveryAddress;
        private String status; // Pending, In Transit, Delivered
        private String scheduledTime; // e.g., ISO string or formatted time
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteInfo {
        private String startLocation;
        private String endLocation;
        private List<RouteStop> stops;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteStop {
        private String address;
        private String expectedTime; // optional
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationInfo {
        private String message;
        private String timestamp; // optional ISO string
        private String type; // info, warning, error
    }
}
