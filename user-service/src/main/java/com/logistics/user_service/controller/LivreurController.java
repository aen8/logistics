package com.logistics.user_service.controller;

import com.logistics.user_service.dtos.UpdateLivreurPositionRequest;
import com.logistics.user_service.dtos.response.LivreurResponse;
import com.logistics.user_service.service.LivreurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/livreurs")
@RequiredArgsConstructor
public class LivreurController {
  private final LivreurService livreurService ;

    @GetMapping("/{id}")
    public ResponseEntity<LivreurResponse> getLivreurById(@PathVariable Long id) {
        LivreurResponse response = livreurService.getLivreurById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<LivreurResponse>> getAllLivreurs() {
        List<LivreurResponse> livreurs = livreurService.getAllLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/available")
    public ResponseEntity<List<LivreurResponse>> getAvailableLivreurs() {
        List<LivreurResponse> livreurs = livreurService.getAvailableLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    @PutMapping("/{id}/position")
    @PreAuthorize("hasRole('LIVREUR') and @userSecurity.isOwner(#id)")
    public ResponseEntity<LivreurResponse> updatePosition(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateLivreurPositionRequest request) {
        LivreurResponse response = livreurService.updatePosition(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/disponibilite")
    @PreAuthorize("hasRole('LIVREUR') and @userSecurity.isOwner(#id)")
    public ResponseEntity<LivreurResponse> updateDisponibilite(@PathVariable Long id,
                                                               @RequestParam Boolean disponibilite) {
        LivreurResponse response = livreurService.updateDisponibilite(id, disponibilite);
        return ResponseEntity.ok(response);
    }



}
