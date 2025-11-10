package com.logistics.delivery_service.client;

import com.logistics.delivery_service.dtos.requestDTOs.RouteCalculationRequest;
import com.logistics.delivery_service.dtos.responseDTOs.RouteCalculationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "route-service")
public interface RouteServiceClient {

    @PostMapping("/api/routes/calculate")
    RouteCalculationResponse calculateRoute(@RequestBody RouteCalculationRequest request);
}