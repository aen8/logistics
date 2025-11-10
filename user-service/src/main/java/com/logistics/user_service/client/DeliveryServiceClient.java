package com.logistics.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// Add configuration = FeignClientConfig.class to enable the interceptor
@FeignClient(name = "delivery-service", configuration = FeignClientConfig.class)
public interface DeliveryServiceClient {

    @GetMapping("/api/deliveries/number") // full path of your endpoint
    long getHowManyLivraison();
}
