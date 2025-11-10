package com.logistics.delivery_service.client;

import com.logistics.delivery_service.dtos.AdresseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "warehouse-service")
public interface WarehouseServiceClient {

    @GetMapping("/api/warehouses/{id}/address")
    AdresseDTO getWarehouseAddress(@PathVariable("id") Long id);

    @GetMapping("/api/warehouses/nearest")
    Long getNearestWarehouse(@RequestParam Double latitude, @RequestParam Double longitude);
}