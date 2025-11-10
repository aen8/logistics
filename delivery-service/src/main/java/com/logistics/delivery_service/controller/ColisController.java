package com.logistics.delivery_service.controller;

import com.logistics.delivery_service.dtos.responseDTOs.ColisResponse;
import com.logistics.delivery_service.enums.EtatColis;
import com.logistics.delivery_service.service.ColisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class ColisController {

    private final ColisService colisService ;

    @GetMapping("/{id}")
    public ResponseEntity<ColisResponse> getColisById(@PathVariable Long id) {
        ColisResponse response = colisService.getColisById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<ColisResponse> getColisByTrackingCode(@PathVariable String trackingCode) {
        ColisResponse response = colisService.getColisByTrackingCode(trackingCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delivery/{livraisonId}")
    public ResponseEntity<List<ColisResponse>> getColisByLivraison(@PathVariable Long livraisonId) {
        List<ColisResponse> colis = colisService.getColisByLivraison(livraisonId);
        return ResponseEntity.ok(colis);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ColisResponse> updateColisStatus(@PathVariable Long id,
                                                           @RequestParam EtatColis etat,
                                                           @RequestParam(required = false) String notes) {
        ColisResponse response = colisService.updateColisStatus(id, etat, notes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/scan")
    public ResponseEntity<ColisResponse> scanColis(@PathVariable Long id) {
        ColisResponse response = colisService.scanColis(id);
        return ResponseEntity.ok(response);
    }
}