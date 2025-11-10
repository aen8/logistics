package com.logistics.user_service.client;

import com.logistics.user_service.dtos.response.LivreurDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/orders/livreur/{livreurId}/stats")
    LivreurDashboardResponse.DashboardStats getOrderStats(@PathVariable Long livreurId);
}
