package com.logistics.tracking_service.service;


import com.logistics.tracking_service.dtos.requestDTOs.GPSPositionRequest;
import com.logistics.tracking_service.dtos.responseDTOs.GPSPositionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final GPSPositionService gpsPositionService;
    private final SimpMessagingTemplate messagingTemplate;

    // Cache des sessions actives (livreurId -> sessionId)
    private final Map<Long, String> activeSessions = new ConcurrentHashMap<>();

    // Cache des dernières positions (pour statistiques)
    private final Map<Long, GPSPositionResponse> lastPositions = new ConcurrentHashMap<>();

    /**
     * Enregistre une position et diffuse en temps réel via WebSocket
     */
    public GPSPositionResponse trackPosition(GPSPositionRequest request) {
        log.info("Tracking position for livreur: {}", request.getLivreurId());

        // Valider la position
        validatePosition(request);

        // Sauvegarder la position
        GPSPositionResponse response = gpsPositionService.savePosition(request);

        // Mettre à jour le cache local
        lastPositions.put(request.getLivreurId(), response);

        // Diffuser via WebSocket
        broadcastPosition(response);

        // Vérifier les alertes (vitesse excessive, zone dangereuse, etc.)
        checkAlerts(response);

        return response;
    }

    /**
     * Enregistrer une session WebSocket active
     */
    public void registerSession(Long livreurId, String sessionId) {
        log.info("Registering WebSocket session for livreur: {} -> {}", livreurId, sessionId);
        activeSessions.put(livreurId, sessionId);
    }

    /**
     * Désenregistrer une session WebSocket
     */
    public void unregisterSession(String sessionId) {
        log.info("Unregistering WebSocket session: {}", sessionId);
        activeSessions.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));
    }

    /**
     * Récupérer le nombre de sessions actives
     */
    public int getActiveSessionsCount() {
        return activeSessions.size();
    }

    /**
     * Récupérer les dernières positions en cache
     */
    public Map<Long, GPSPositionResponse> getLastPositions() {
        return new HashMap<>(lastPositions);
    }

    /**
     * Diffuse une position via WebSocket sur plusieurs topics
     */
    private void broadcastPosition(GPSPositionResponse position) {
        try {
            // Topic général pour tous les livreurs
            messagingTemplate.convertAndSend("/topic/positions", position);
            log.debug("Position broadcasted to /topic/positions");

            // Topic spécifique au livreur
            messagingTemplate.convertAndSend(
                    "/topic/livreur/" + position.getLivreurId(),
                    position
            );
            log.debug("Position broadcasted to /topic/livreur/{}", position.getLivreurId());

            // Si associé à une livraison, diffuser aussi sur ce topic
            if (position.getLivraisonId() != null) {
                messagingTemplate.convertAndSend(
                        "/topic/livraison/" + position.getLivraisonId(),
                        position
                );
                log.debug("Position broadcasted to /topic/livraison/{}", position.getLivraisonId());
            }

            // Topic pour les positions en mouvement uniquement
            if (Boolean.TRUE.equals(position.getIsMoving())) {
                messagingTemplate.convertAndSend("/topic/positions/moving", position);
            }

        } catch (Exception e) {
            log.error("Error broadcasting position: {}", e.getMessage(), e);
        }
    }

    /**
     * Valider les données de position GPS
     */
    private void validatePosition(GPSPositionRequest request) {
        if (request.getLatitude() < -90 || request.getLatitude() > 90) {
            throw new IllegalArgumentException("Invalid latitude: " + request.getLatitude());
        }
        if (request.getLongitude() < -180 || request.getLongitude() > 180) {
            throw new IllegalArgumentException("Invalid longitude: " + request.getLongitude());
        }
        if (request.getVitesse() != null && (request.getVitesse() < 0 || request.getVitesse() > 300)) {
            throw new IllegalArgumentException("Invalid speed: " + request.getVitesse());
        }
    }

    /**
     * Vérifier les alertes basées sur la position
     */
    private void checkAlerts(GPSPositionResponse position) {
        try {
            // Alerte vitesse excessive (> 120 km/h)
            if (position.getVitesse() != null && position.getVitesse() > 120) {
                sendAlert(position.getLivreurId(), "EXCESSIVE_SPEED",
                        "Vitesse excessive détectée: " + position.getVitesse() + " km/h");
            }

            // Alerte batterie faible (< 20%)
            if (position.getBatteryLevel() != null && position.getBatteryLevel() < 20) {
                sendAlert(position.getLivreurId(), "LOW_BATTERY",
                        "Batterie faible: " + position.getBatteryLevel() + "%");
            }

            // Alerte position stationnaire prolongée
            GPSPositionResponse lastPos = lastPositions.get(position.getLivreurId());
            if (lastPos != null && !position.getIsMoving() && !lastPos.getIsMoving()) {
                long minutesStopped = java.time.Duration.between(
                        lastPos.getDateHeure(),
                        position.getDateHeure()
                ).toMinutes();

                if (minutesStopped > 30) {
                    sendAlert(position.getLivreurId(), "LONG_STOP",
                            "Arrêt prolongé détecté: " + minutesStopped + " minutes");
                }
            }

        } catch (Exception e) {
            log.error("Error checking alerts: {}", e.getMessage());
        }
    }

    /**
     * Envoyer une alerte via WebSocket
     */
    private void sendAlert(Long livreurId, String alertType, String message) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("livreurId", livreurId);
        alert.put("type", alertType);
        alert.put("message", message);
        alert.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/alerts", alert);
        messagingTemplate.convertAndSend("/topic/alerts/livreur/" + livreurId, alert);

        log.warn("Alert sent for livreur {}: {} - {}", livreurId, alertType, message);
    }

    /**
     * Diffuser les statistiques en temps réel
     */
    public void broadcastStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("activeLivreurs", activeSessions.size());
            stats.put("trackedLivreurs", lastPositions.size());
            stats.put("timestamp", LocalDateTime.now());

            // Calculer la vitesse moyenne
            double avgSpeed = lastPositions.values().stream()
                    .filter(p -> p.getVitesse() != null && p.getVitesse() > 0)
                    .mapToDouble(GPSPositionResponse::getVitesse)
                    .average()
                    .orElse(0.0);
            stats.put("averageSpeed", Math.round(avgSpeed * 100.0) / 100.0);

            // Nombre de livreurs en mouvement
            long movingCount = lastPositions.values().stream()
                    .filter(p -> Boolean.TRUE.equals(p.getIsMoving()))
                    .count();
            stats.put("movingLivreurs", movingCount);

            messagingTemplate.convertAndSend("/topic/statistics", stats);
            log.debug("Statistics broadcasted: {}", stats);

        } catch (Exception e) {
            log.error("Error broadcasting statistics: {}", e.getMessage());
        }
    }

    /**
     * Nettoyer les positions obsolètes du cache local
     */
    public void cleanupStalePositions(int minutes) {
        log.info("Cleaning up stale positions older than {} minutes", minutes);

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutes);
        lastPositions.entrySet().removeIf(entry ->
                entry.getValue().getDateHeure().isBefore(cutoff)
        );

        log.info("Stale positions cleaned. Remaining: {}", lastPositions.size());
    }
}