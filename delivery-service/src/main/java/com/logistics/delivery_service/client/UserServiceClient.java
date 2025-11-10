package com.logistics.delivery_service.client;


import com.logistics.delivery_service.dtos.responseDTOs.LivreurDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/livreurs/available")
    List<LivreurDTO> getAvailableLivreurs();

    @GetMapping("/api/users/livreurs/{id}")
    LivreurDTO getLivreurById(@PathVariable("id") Long id);

    @PutMapping("/api/users/livreurs/{id}/disponibilite")
    void updateLivreurDisponibilite(@PathVariable("id") Long id, @RequestParam Boolean disponibilite);
}