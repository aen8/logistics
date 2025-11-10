package com.logistics.tracking_service.controller;



import com.logistics.tracking_service.dtos.requestDTOs.GPSPositionRequest;
import com.logistics.tracking_service.dtos.responseDTOs.GPSPositionResponse;
import com.logistics.tracking_service.service.GPSPositionService;
import com.logistics.tracking_service.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking/positions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GPSPositionController {

    private final GPSPositionService gpsPositionService;
    private final TrackingService trackingService;

    /**
     * Enregistrer une nouvelle position GPS
     *
     * POST /api/tracking/positions
     */
    @PostMapping
    public ResponseEntity<GPSPositionResponse> createPosition(
            @Valid @RequestBody GPSPositionRequest request) {

        log.info("POST /api/tracking/positions - Creating position for livreur: {}",
                request.getLivreurId());

        GPSPositionResponse response = trackingService.trackPosition(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Enregistrer plusieurs positions en batch
     *
     * POST /api/tracking/positions/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<List<GPSPositionResponse>> createPositionsBatch(
            @Valid @RequestBody List<GPSPositionRequest> requests) {

        log.info("POST /api/tracking/positions/batch - Creating {} positions", requests.size());

        List<GPSPositionResponse> responses = requests.stream()
                .map(trackingService::trackPosition)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * Récupérer la dernière position d'un livreur
     *
     * GET /api/tracking/positions/livreur/{livreurId}/last
     */
    @GetMapping("/livreur/{livreurId}/last")
    public ResponseEntity<GPSPositionResponse> getLastPositionByLivreur(
            @PathVariable Long livreurId) {

        log.info("GET /api/tracking/positions/livreur/{}/last", livreurId);
        GPSPositionResponse response = gpsPositionService.getLastPositionByLivreur(livreurId);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer l'historique des positions d'un livreur (dernières X heures)
     *
     * GET /api/tracking/positions/livreur/{livreurId}/history?hours=24
     */
    @GetMapping("/livreur/{livreurId}/history")
    public ResponseEntity<List<GPSPositionResponse>> getPositionHistoryByLivreur(
            @PathVariable Long livreurId,
            @RequestParam(required = false, defaultValue = "24") Integer hours) {

        log.info("GET /api/tracking/positions/livreur/{}/history?hours={}", livreurId, hours);
        List<GPSPositionResponse> positions = gpsPositionService
                .getPositionHistoryByLivreur(livreurId, hours);
        return ResponseEntity.ok(positions);
    }

    /**
     * Récupérer toutes les positions d'un livreur
     *
     * GET /api/tracking/positions/livreur/{livreurId}
     */
    @GetMapping("/livreur/{livreurId}")
    public ResponseEntity<List<GPSPositionResponse>> getAllPositionsByLivreur(
            @PathVariable Long livreurId) {

        log.info("GET /api/tracking/positions/livreur/{}", livreurId);
        List<GPSPositionResponse> positions = gpsPositionService
                .getAllPositionsByLivreur(livreurId);
        return ResponseEntity.ok(positions);
    }

    /**
     * Récupérer toutes les positions d'une livraison
     *
     * GET /api/tracking/positions/livraison/{livraisonId}
     */
    @GetMapping("/livraison/{livraisonId}")
    public ResponseEntity<List<GPSPositionResponse>> getPositionsByLivraison(
            @PathVariable Long livraisonId) {

        log.info("GET /api/tracking/positions/livraison/{}", livraisonId);
        List<GPSPositionResponse> positions = gpsPositionService
                .getPositionsByLivraison(livraisonId);
        return ResponseEntity.ok(positions);
    }

    /**
     * Récupérer la dernière position d'une livraison
     *
     * GET /api/tracking/positions/livraison/{livraisonId}/last
     */
    @GetMapping("/livraison/{livraisonId}/last")
    public ResponseEntity<GPSPositionResponse> getLastPositionByLivraison(
            @PathVariable Long livraisonId) {

        log.info("GET /api/tracking/positions/livraison/{}/last", livraisonId);
        GPSPositionResponse response = gpsPositionService
                .getLastPositionByLivraison(livraisonId);
        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer les IDs des livreurs actifs (positions récentes)
     *
     * GET /api/tracking/positions/active-livreurs?minutes=30
     */
    @GetMapping("/active-livreurs")
    public ResponseEntity<List<Long>> getActiveLivreurs(
            @RequestParam(required = false, defaultValue = "30") Integer minutes) {

        log.info("GET /api/tracking/positions/active-livreurs?minutes={}", minutes);
        List<Long> livreurIds = gpsPositionService.getActiveLivreurs(minutes);
        return ResponseEntity.ok(livreurIds);
    }

    /**
     * Récupérer les positions dans une zone géographique
     *
     * GET /api/tracking/positions/area?latitude=33.5731&longitude=-7.5898&radiusKm=5&minutes=30
     */
    @GetMapping("/area")
    public ResponseEntity<List<GPSPositionResponse>> getPositionsInArea(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(required = false, defaultValue = "30") Integer minutes) {

        log.info("GET /api/tracking/positions/area?lat={}&lon={}&radius={}&minutes={}",
                latitude, longitude, radiusKm, minutes);

        List<GPSPositionResponse> positions = gpsPositionService.getPositionsInArea(
                latitude, longitude, radiusKm, minutes
        );
        return ResponseEntity.ok(positions);
    }

    /**
     * Compter le nombre de positions d'un livreur
     *
     * GET /api/tracking/positions/livreur/{livreurId}/count
     */
    @GetMapping("/livreur/{livreurId}/count")
    public ResponseEntity<Map<String, Object>> countPositionsByLivreur(
            @PathVariable Long livreurId) {

        log.info("GET /api/tracking/positions/livreur/{}/count", livreurId);
        Long count = gpsPositionService.countPositionsByLivreur(livreurId);

        Map<String, Object> response = new HashMap<>();
        response.put("livreurId", livreurId);
        response.put("totalPositions", count);

        return ResponseEntity.ok(response);
    }

    /**
     * Calculer la distance parcourue par un livreur sur une période
     *
     * GET /api/tracking/positions/livreur/{livreurId}/distance?startDate=...&endDate=...
     */
    @GetMapping("/livreur/{livreurId}/distance")
    public ResponseEntity<Map<String, Object>> calculateDistanceTraveled(
            @PathVariable Long livreurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("GET /api/tracking/positions/livreur/{}/distance?start={}&end={}",
                livreurId, startDate, endDate);

        Double distance = gpsPositionService.calculateDistanceTraveled(livreurId, startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("livreurId", livreurId);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("distanceKm", distance);

        return ResponseEntity.ok(response);
    }

    /**
     * Récupérer les statistiques globales
     *
     * GET /api/tracking/positions/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("GET /api/tracking/positions/statistics");

        Map<Long, GPSPositionResponse> lastPositions = trackingService.getLastPositions();

        // Calculer les statistiques
        long totalLivreurs = lastPositions.size();
        long movingLivreurs = lastPositions.values().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsMoving()))
                .count();

        double avgSpeed = lastPositions.values().stream()
                .filter(p -> p.getVitesse() != null && p.getVitesse() > 0)
                .mapToDouble(GPSPositionResponse::getVitesse)
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTrackedLivreurs", totalLivreurs);
        stats.put("movingLivreurs", movingLivreurs);
        stats.put("stoppedLivreurs", totalLivreurs - movingLivreurs);
        stats.put("averageSpeed", Math.round(avgSpeed * 100.0) / 100.0);
        stats.put("activeSessions", trackingService.getActiveSessionsCount());
        stats.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(stats);
    }

    /**
     * Supprimer les anciennes positions (endpoint admin)
     *
     * DELETE /api/tracking/positions/cleanup?days=30
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldPositions(
            @RequestParam(required = false, defaultValue = "30") Integer days) {

        log.info("DELETE /api/tracking/positions/cleanup?days={}", days);
        gpsPositionService.deleteOldPositions(days);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Old positions deleted successfully");
        response.put("olderThanDays", days);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * Health check spécifique au tracking
     *
     * GET /api/tracking/positions/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "tracking-service");
        health.put("timestamp", LocalDateTime.now());
        health.put("activeSessions", trackingService.getActiveSessionsCount());
        health.put("trackedLivreurs", trackingService.getLastPositions().size());

        return ResponseEntity.ok(health);
    }
}