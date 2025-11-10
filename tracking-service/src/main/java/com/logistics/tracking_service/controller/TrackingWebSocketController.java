package com.logistics.tracking_service.controller;


import com.logistics.tracking_service.dtos.requestDTOs.GPSPositionRequest;
import com.logistics.tracking_service.dtos.responseDTOs.GPSPositionResponse;
import com.logistics.tracking_service.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller WebSocket pour le tracking GPS en temps réel
 *
 * Architecture STOMP:
 * - Clients envoient à: /app/xxx
 * - Serveur diffuse sur: /topic/xxx ou /queue/xxx
 *
 * Topics disponibles:
 * - /topic/positions : Toutes les positions
 * - /topic/livreur/{id} : Positions d'un livreur spécifique
 * - /topic/livraison/{id} : Positions d'une livraison spécifique
 * - /topic/positions/moving : Positions en mouvement uniquement
 * - /topic/alerts : Toutes les alertes
 * - /topic/statistics : Statistiques en temps réel
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class TrackingWebSocketController {

    private final TrackingService trackingService;

    /**
     * Recevoir et diffuser les positions GPS via WebSocket
     *
     * Client envoie à: /app/track
     * Serveur diffuse à: /topic/positions
     *
     * Exemple client JavaScript:
     * stompClient.send("/app/track", {}, JSON.stringify(positionData));
     */
    @MessageMapping("/track")
    @SendTo("/topic/positions")
    public GPSPositionResponse trackPosition(@Payload GPSPositionRequest request) {
        log.info("WebSocket: Received position from livreur: {}", request.getLivreurId());

        try {
            return trackingService.trackPosition(request);
        } catch (Exception e) {
            log.error("Error tracking position via WebSocket: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to track position: " + e.getMessage());
        }
    }

    /**
     * Recevoir plusieurs positions en batch
     *
     * Client envoie à: /app/track/batch
     * Serveur diffuse à: /topic/positions
     */
    @MessageMapping("/track/batch")
    @SendTo("/topic/positions")
    public Map<String, Object> trackPositionsBatch(@Payload java.util.List<GPSPositionRequest> requests) {
        log.info("WebSocket: Received batch of {} positions", requests.size());

        Map<String, Object> response = new HashMap<>();
        response.put("totalReceived", requests.size());
        response.put("timestamp", LocalDateTime.now());

        try {
            requests.forEach(trackingService::trackPosition);
            response.put("status", "success");
        } catch (Exception e) {
            log.error("Error processing batch: {}", e.getMessage());
            response.put("status", "error");
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * S'abonner aux positions d'un livreur spécifique
     *
     * Client s'abonne à: /topic/livreur/{livreurId}
     *
     * Exemple client JavaScript:
     * stompClient.subscribe('/topic/livreur/1', function(message) {
     *     console.log(JSON.parse(message.body));
     * });
     */
    @SubscribeMapping("/livreur/{livreurId}")
    public GPSPositionResponse subscribeLivreur(@DestinationVariable Long livreurId) {
        log.info("Client subscribed to livreur: {}", livreurId);

        try {
            // Envoyer immédiatement la dernière position connue
            return trackingService.getLastPositions().get(livreurId);
        } catch (Exception e) {
            log.warn("No position found for livreur: {}", livreurId);
            return null;
        }
    }

    /**
     * S'abonner aux positions d'une livraison spécifique
     *
     * Client s'abonne à: /topic/livraison/{livraisonId}
     */
    @SubscribeMapping("/livraison/{livraisonId}")
    public Map<String, Object> subscribeLivraison(@DestinationVariable Long livraisonId) {
        log.info("Client subscribed to livraison: {}", livraisonId);

        Map<String, Object> response = new HashMap<>();
        response.put("livraisonId", livraisonId);
        response.put("subscribedAt", LocalDateTime.now());
        response.put("message", "Successfully subscribed to livraison tracking");

        return response;
    }

    /**
     * S'abonner à toutes les positions
     *
     * Client s'abonne à: /topic/positions
     */
    @SubscribeMapping("/positions")
    public Map<String, Object> subscribeAllPositions() {
        log.info("Client subscribed to all positions");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully subscribed to all positions");
        response.put("currentTracked", trackingService.getLastPositions().size());
        response.put("subscribedAt", LocalDateTime.now());

        return response;
    }

    /**
     * S'abonner aux positions en mouvement uniquement
     *
     * Client s'abonne à: /topic/positions/moving
     */
    @SubscribeMapping("/positions/moving")
    public Map<String, Object> subscribeMovingPositions() {
        log.info("Client subscribed to moving positions only");

        long movingCount = trackingService.getLastPositions().values().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsMoving()))
                .count();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully subscribed to moving positions");
        response.put("currentMoving", movingCount);
        response.put("subscribedAt", LocalDateTime.now());

        return response;
    }

    /**
     * S'abonner aux alertes
     *
     * Client s'abonne à: /topic/alerts
     */
    @SubscribeMapping("/alerts")
    public Map<String, Object> subscribeAlerts() {
        log.info("Client subscribed to alerts");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully subscribed to alerts");
        response.put("subscribedAt", LocalDateTime.now());

        return response;
    }

    /**
     * S'abonner aux alertes d'un livreur spécifique
     *
     * Client s'abonne à: /topic/alerts/livreur/{livreurId}
     */
    @SubscribeMapping("/alerts/livreur/{livreurId}")
    public Map<String, Object> subscribeAlertsForLivreur(@DestinationVariable Long livreurId) {
        log.info("Client subscribed to alerts for livreur: {}", livreurId);

        Map<String, Object> response = new HashMap<>();
        response.put("livreurId", livreurId);
        response.put("message", "Successfully subscribed to livreur alerts");
        response.put("subscribedAt", LocalDateTime.now());

        return response;
    }

    /**
     * S'abonner aux statistiques en temps réel
     *
     * Client s'abonne à: /topic/statistics
     */
    @SubscribeMapping("/statistics")
    public Map<String, Object> subscribeStatistics() {
        log.info("Client subscribed to statistics");

        Map<Long, GPSPositionResponse> lastPositions = trackingService.getLastPositions();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTracked", lastPositions.size());
        stats.put("activeSessions", trackingService.getActiveSessionsCount());

        long movingCount = lastPositions.values().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsMoving()))
                .count();
        stats.put("movingLivreurs", movingCount);
        stats.put("stoppedLivreurs", lastPositions.size() - movingCount);

        double avgSpeed = lastPositions.values().stream()
                .filter(p -> p.getVitesse() != null && p.getVitesse() > 0)
                .mapToDouble(GPSPositionResponse::getVitesse)
                .average()
                .orElse(0.0);
        stats.put("averageSpeed", Math.round(avgSpeed * 100.0) / 100.0);

        stats.put("timestamp", LocalDateTime.now());

        return stats;
    }

    /**
     * Commande pour demander les statistiques actuelles
     *
     * Client envoie à: /app/statistics/request
     * Serveur diffuse à: /topic/statistics
     */
    @MessageMapping("/statistics/request")
    @SendTo("/topic/statistics")
    public Map<String, Object> requestStatistics() {
        log.info("WebSocket: Statistics requested");
        trackingService.broadcastStatistics();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Statistics update requested");
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    /**
     * Ping/Pong pour vérifier la connexion WebSocket
     *
     * Client envoie à: /app/ping
     * Serveur répond à: /topic/pong
     */
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public Map<String, Object> ping() {
        log.debug("WebSocket: Ping received");

        Map<String, Object> pong = new HashMap<>();
        pong.put("message", "pong");
        pong.put("timestamp", LocalDateTime.now());
        pong.put("serverStatus", "UP");

        return pong;
    }
}