package com.logistics.warehouse_service.controller;

import com.logistics.warehouse_service.dtos.AdresseDTO;
import com.logistics.warehouse_service.dtos.requestDTOs.EntrepotRequest;
import com.logistics.warehouse_service.dtos.requestDTOs.UpdateEntrepotRequest;
import com.logistics.warehouse_service.dtos.responseDTOs.EntrepotResponse;
import com.logistics.warehouse_service.dtos.responseDTOs.EntrepotStatsResponse;
import com.logistics.warehouse_service.enums.StatutEntrepot;
import com.logistics.warehouse_service.enums.TypeEntrepot;
import com.logistics.warehouse_service.service.EntrepotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class EntrepotController {

    private final EntrepotService entrepotService;

    @PostMapping
    public ResponseEntity<EntrepotResponse> createEntrepot(@Valid @RequestBody EntrepotRequest request) {
        EntrepotResponse response = entrepotService.createEntrepot(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrepotResponse> getEntrepotById(@PathVariable Long id) {
        EntrepotResponse response = entrepotService.getEntrepotById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<EntrepotResponse> getEntrepotByCode(@PathVariable String code) {
        EntrepotResponse response = entrepotService.getEntrepotByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EntrepotResponse>> getAllEntrepots() {
        List<EntrepotResponse> entrepots = entrepotService.getAllEntrepots();
        return ResponseEntity.ok(entrepots);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EntrepotResponse>> getEntrepotsByStatus(@PathVariable StatutEntrepot status) {
        List<EntrepotResponse> entrepots = entrepotService.getEntrepotsByStatus(status);
        return ResponseEntity.ok(entrepots);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<EntrepotResponse>> getEntrepotsByType(@PathVariable TypeEntrepot type) {
        List<EntrepotResponse> entrepots = entrepotService.getEntrepotsByType(type);
        return ResponseEntity.ok(entrepots);
    }

    @GetMapping("/city/{ville}")
    public ResponseEntity<List<EntrepotResponse>> getEntrepotsByVille(@PathVariable String ville) {
        List<EntrepotResponse> entrepots = entrepotService.getEntrepotsByVille(ville);
        return ResponseEntity.ok(entrepots);
    }

    @GetMapping("/available")
    public ResponseEntity<List<EntrepotResponse>> getAvailableWarehouses(@RequestParam int capacity) {
        List<EntrepotResponse> entrepots = entrepotService.getAvailableWarehouses(capacity);
        return ResponseEntity.ok(entrepots);
    }

    @GetMapping("/nearest")
    public ResponseEntity<EntrepotResponse> getNearestWarehouse(@RequestParam Double latitude,
                                                                @RequestParam Double longitude) {
        EntrepotResponse response = entrepotService.getNearestWarehouse(latitude, longitude);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/address")
    public ResponseEntity<AdresseDTO> getWarehouseAddress(@PathVariable Long id) {
        AdresseDTO address = entrepotService.getWarehouseAddress(id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<EntrepotStatsResponse> getWarehouseStats(@PathVariable Long id) {
        EntrepotStatsResponse stats = entrepotService.getWarehouseStats(id);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntrepotResponse> updateEntrepot(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateEntrepotRequest request) {
        EntrepotResponse response = entrepotService.updateEntrepot(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntrepot(@PathVariable Long id) {
        entrepotService.deleteEntrepot(id);
        return ResponseEntity.noContent().build();
    }
}