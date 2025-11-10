package com.logistics.tracking_service.service;



import com.logistics.tracking_service.dtos.requestDTOs.GPSPositionRequest;
import com.logistics.tracking_service.dtos.responseDTOs.GPSPositionResponse;
import com.logistics.tracking_service.model.GPSPosition;
import com.logistics.tracking_service.repository.GPSPositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GPSPositionService {

    private final GPSPositionRepository gpsPositionRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "gps:position:";
    private static final long CACHE_TTL = 300; // 5 minutes

    /**
     * Enregistrer une nouvelle position GPS
     */
    @Transactional
    @CacheEvict(value = "gpsPositions", key = "#request.livreurId")
    public GPSPositionResponse savePosition(GPSPositionRequest request) {
        log.info("Saving GPS position for livreur: {}", request.getLivreurId());

        // Convertir Request -> Entity
        GPSPosition gpsPosition = toEntity(request);

        // Sauvegarder
        GPSPosition saved = gpsPositionRepository.save(gpsPosition);

        // Convertir Entity -> Response
        GPSPositionResponse response = toResponse(saved);

        // Mettre en cache dans Redis
        String redisKey = REDIS_KEY_PREFIX + request.getLivreurId();
        redisTemplate.opsForValue().set(redisKey, response, CACHE_TTL, TimeUnit.SECONDS);

        log.info("GPS position saved with ID: {}", saved.getIdPosition());
        return response;
    }

    /**
     * Récupérer la dernière position d'un livreur
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "gpsPositions", key = "#livreurId")
    public GPSPositionResponse getLastPositionByLivreur(Long livreurId) {
        log.info("Fetching last position for livreur: {}", livreurId);

        // Essayer d'abord depuis Redis
        String redisKey = REDIS_KEY_PREFIX + livreurId;
        Object cached = redisTemplate.opsForValue().get(redisKey);
        if (cached instanceof GPSPositionResponse) {
            log.debug("Position found in Redis cache");
            return (GPSPositionResponse) cached;
        }

        // Si pas en cache, récupérer depuis la DB
        GPSPosition position = gpsPositionRepository
                .findTopByLivreurIdOrderByDateHeureDesc(livreurId)
                .orElseThrow(() -> new RuntimeException("No GPS position found for livreur: " + livreurId));

        GPSPositionResponse response = toResponse(position);

        // Mettre en cache
        redisTemplate.opsForValue().set(redisKey, response, CACHE_TTL, TimeUnit.SECONDS);

        return response;
    }

    /**
     * Récupérer l'historique des positions d'un livreur
     */
    @Transactional(readOnly = true)
    public List<GPSPositionResponse> getPositionHistoryByLivreur(Long livreurId, Integer hours) {
        log.info("Fetching position history for livreur: {} (last {} hours)", livreurId, hours);

        LocalDateTime since = LocalDateTime.now().minusHours(hours != null ? hours : 24);
        List<GPSPosition> positions = gpsPositionRepository
                .findByLivreurIdAndDateHeureBetweenOrderByDateHeureDesc(
                        livreurId,
                        since,
                        LocalDateTime.now()
                );

        return toResponseList(positions);
    }

    /**
     * Récupérer toutes les positions d'un livreur
     */
    @Transactional(readOnly = true)
    public List<GPSPositionResponse> getAllPositionsByLivreur(Long livreurId) {
        log.info("Fetching all positions for livreur: {}", livreurId);

        List<GPSPosition> positions = gpsPositionRepository
                .findByLivreurIdOrderByDateHeureDesc(livreurId);

        return toResponseList(positions);
    }

    /**
     * Récupérer toutes les positions d'une livraison
     */
    @Transactional(readOnly = true)
    public List<GPSPositionResponse> getPositionsByLivraison(Long livraisonId) {
        log.info("Fetching positions for livraison: {}", livraisonId);

        List<GPSPosition> positions = gpsPositionRepository
                .findByLivraisonIdOrderByDateHeureDesc(livraisonId);

        return toResponseList(positions);
    }

    /**
     * Récupérer la dernière position d'une livraison
     */
    @Transactional(readOnly = true)
    public GPSPositionResponse getLastPositionByLivraison(Long livraisonId) {
        log.info("Fetching last position for livraison: {}", livraisonId);

        GPSPosition position = gpsPositionRepository
                .findTopByLivraisonIdOrderByDateHeureDesc(livraisonId)
                .orElseThrow(() -> new RuntimeException("No GPS position found for livraison: " + livraisonId));

        return toResponse(position);
    }

    /**
     * Récupérer les IDs des livreurs actifs
     */
    @Transactional(readOnly = true)
    public List<Long> getActiveLivreurs(Integer minutes) {
        log.info("Fetching active livreurs (last {} minutes)", minutes);

        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes != null ? minutes : 30);
        return gpsPositionRepository.findActiveLivreurs(since);
    }

    /**
     * Supprimer les positions plus anciennes que X jours
     */
    @Transactional
    public void deleteOldPositions(Integer days) {
        log.info("Deleting GPS positions older than {} days", days);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days != null ? days : 30);
        gpsPositionRepository.deleteByDateHeureBefore(cutoffDate);

        log.info("Old GPS positions deleted successfully");
    }

    /**
     * Récupérer les positions dans une zone géographique
     */
    @Transactional(readOnly = true)
    public List<GPSPositionResponse> getPositionsInArea(
            Double centerLat, Double centerLon, Double radiusKm, Integer minutes) {

        log.info("Fetching positions in area: lat={}, lon={}, radius={}km",
                centerLat, centerLon, radiusKm);

        // Calculer les bounds approximatifs (1° ≈ 111km)
        Double latOffset = radiusKm / 111.0;
        Double lonOffset = radiusKm / (111.0 * Math.cos(Math.toRadians(centerLat)));

        Double minLat = centerLat - latOffset;
        Double maxLat = centerLat + latOffset;
        Double minLon = centerLon - lonOffset;
        Double maxLon = centerLon + lonOffset;

        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes != null ? minutes : 30);

        List<GPSPosition> positions = gpsPositionRepository.findPositionsInBounds(
                minLat, maxLat, minLon, maxLon, since
        );

        return toResponseList(positions);
    }

    /**
     * Compter le nombre total de positions d'un livreur
     */
    @Transactional(readOnly = true)
    public Long countPositionsByLivreur(Long livreurId) {
        log.info("Counting positions for livreur: {}", livreurId);
        return gpsPositionRepository.countByLivreurId(livreurId);
    }

    /**
     * Calculer la distance parcourue par un livreur sur une période
     */
    @Transactional(readOnly = true)
    public Double calculateDistanceTraveled(Long livreurId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating distance for livreur: {} between {} and {}",
                livreurId, startDate, endDate);

        List<GPSPosition> positions = gpsPositionRepository
                .findByLivreurIdAndDateHeureBetweenOrderByDateHeureDesc(livreurId, startDate, endDate);

        if (positions.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        for (int i = 0; i < positions.size() - 1; i++) {
            GPSPosition p1 = positions.get(i);
            GPSPosition p2 = positions.get(i + 1);
            totalDistance += calculateHaversineDistance(
                    p1.getLatitude(), p1.getLongitude(),
                    p2.getLatitude(), p2.getLongitude()
            );
        }

        log.info("Total distance calculated: {} km", totalDistance);
        return Math.round(totalDistance * 100.0) / 100.0;
    }

    /**
     * Calculer la distance entre deux points avec la formule de Haversine
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS_KM = 6371.0;

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    // ==================== MÉTHODES DE MAPPING MANUELLES ====================

    /**
     * Convertir GPSPositionRequest vers GPSPosition (Entity)
     */
    private GPSPosition toEntity(GPSPositionRequest request) {
        GPSPosition entity = new GPSPosition();
        entity.setLivreurId(request.getLivreurId());
        entity.setLivraisonId(request.getLivraisonId());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setVitesse(request.getVitesse());
        entity.setAltitude(request.getAltitude());
        entity.setPrecision(request.getPrecision());
        entity.setAdresse(request.getAdresse());
        entity.setBatteryLevel(request.getBatteryLevel());
        entity.setIsMoving(request.getIsMoving() != null ? request.getIsMoving() : false);
        entity.setDateHeure(LocalDateTime.now());
        return entity;
    }

    /**
     * Convertir GPSPosition (Entity) vers GPSPositionResponse (DTO)
     */
    private GPSPositionResponse toResponse(GPSPosition entity) {
        return GPSPositionResponse.builder()
                .idPosition(entity.getIdPosition())
                .livreurId(entity.getLivreurId())
                .livraisonId(entity.getLivraisonId())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .dateHeure(entity.getDateHeure())
                .vitesse(entity.getVitesse())
                .altitude(entity.getAltitude())
                .precision(entity.getPrecision())
                .adresse(entity.getAdresse())
                .batteryLevel(entity.getBatteryLevel())
                .isMoving(entity.getIsMoving())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Convertir une liste de GPSPosition vers une liste de GPSPositionResponse
     */
    private List<GPSPositionResponse> toResponseList(List<GPSPosition> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}