package com.logistics.route_service.service;

import com.logistics.route_service.dtos.LocationDTO;
import com.logistics.route_service.dtos.requestDTOs.OptimizeRouteRequest;
import com.logistics.route_service.dtos.requestDTOs.RouteCalculationRequest;
import com.logistics.route_service.dtos.responseDTOs.EtapeResponse;
import com.logistics.route_service.dtos.responseDTOs.OptimizedRouteResponse;
import com.logistics.route_service.dtos.responseDTOs.RouteCalculationResponse;
import com.logistics.route_service.enums.StatutItineraire;
import com.logistics.route_service.enums.TypeCalcul;
import com.logistics.route_service.exception.RouteCalculationException;
import com.logistics.route_service.exception.RouteNotFoundException;
import com.logistics.route_service.model.Etape;
import com.logistics.route_service.model.Itineraire;
import com.logistics.route_service.repository.ItineraireRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteService {

    private final ItineraireRepository itineraireRepository;
    private final RouteOptimizer routeOptimizer;
    private final DistanceCalculator distanceCalculator;
    private final PricingCalculator pricingCalculator;

    @Value("${route.average-speed:50.0}")
    private Double averageSpeed;

    @Value("${route.max-distance:500.0}")
    private Double maxDistance;

    @Transactional
    public RouteCalculationResponse calculateRoute(RouteCalculationRequest request) {
        log.info("Calculating route from ({}, {}) to ({}, {})",
                request.getOrigine().getLatitude(), request.getOrigine().getLongitude(),
                request.getDestination().getLatitude(), request.getDestination().getLongitude());

        // Calculer la distance
        double distance = distanceCalculator.calculateDistance(
                request.getOrigine().getLatitude(), request.getOrigine().getLongitude(),
                request.getDestination().getLatitude(), request.getDestination().getLongitude()
        );

        // Vérifier la distance maximale
        if (distance > maxDistance) {
            throw new RouteCalculationException(
                    String.format("Distance exceeds maximum allowed: %.2f km > %.2f km", distance, maxDistance)
            );
        }

        // Calculer la durée
        int duration = (int) ((distance / averageSpeed) * 60); // Convertir en minutes

        // Calculer le coût
        double cost = pricingCalculator.calculateCost(distance, request.getTypeCalcul());

        // Créer l'itinéraire
        Itineraire itineraire = new Itineraire();
        itineraire.setLivraisonId(request.getLivraisonId());
        itineraire.setTypeCalcul(request.getTypeCalcul() != null ? request.getTypeCalcul() : TypeCalcul.DISTANCE_COURTE);
        itineraire.setStatut(StatutItineraire.CALCULE);

        // Points de départ et d'arrivée
        itineraire.setLatitudeDepart(request.getOrigine().getLatitude());
        itineraire.setLongitudeDepart(request.getOrigine().getLongitude());
        itineraire.setAdresseDepart(request.getOrigine().getAdresse());

        itineraire.setLatitudeArrivee(request.getDestination().getLatitude());
        itineraire.setLongitudeArrivee(request.getDestination().getLongitude());
        itineraire.setAdresseArrivee(request.getDestination().getAdresse());

        // Métriques
        itineraire.setDistanceKm(distance);
        itineraire.setDureeMinutes(duration);
        itineraire.setCoutEstime(cost);
        itineraire.setCoutCarburant(pricingCalculator.calculateFuelCost(distance));
        itineraire.setEmissionsCo2(pricingCalculator.calculateCO2Emissions(distance));
        itineraire.setVitesseMoyenne(averageSpeed);

        // Ajouter les étapes intermédiaires si présentes
        if (request.getEtapesIntermediaires() != null && !request.getEtapesIntermediaires().isEmpty()) {
            int ordre = 1;

            // Étape de départ
            Etape etapeDepart = createEtape(request.getOrigine(), ordre++, 0.0, 0);
            itineraire.addEtape(etapeDepart);

            // Étapes intermédiaires
            LocationDTO previousLocation = request.getOrigine();
            for (LocationDTO intermediate : request.getEtapesIntermediaires()) {
                double segmentDistance = distanceCalculator.calculateDistance(
                        previousLocation.getLatitude(), previousLocation.getLongitude(),
                        intermediate.getLatitude(), intermediate.getLongitude()
                );
                int segmentDuration = (int) ((segmentDistance / averageSpeed) * 60);

                Etape etape = createEtape(intermediate, ordre++, segmentDistance, segmentDuration);
                itineraire.addEtape(etape);

                previousLocation = intermediate;
            }

            // Étape d'arrivée
            double lastSegmentDistance = distanceCalculator.calculateDistance(
                    previousLocation.getLatitude(), previousLocation.getLongitude(),
                    request.getDestination().getLatitude(), request.getDestination().getLongitude()
            );
            int lastSegmentDuration = (int) ((lastSegmentDistance / averageSpeed) * 60);
            Etape etapeArrivee = createEtape(request.getDestination(), ordre, lastSegmentDistance, lastSegmentDuration);
            itineraire.addEtape(etapeArrivee);
        }

        // Sauvegarder
        Itineraire savedItineraire = itineraireRepository.save(itineraire);
        log.info("Route calculated with ID: {}", savedItineraire.getIdItineraire());

        return mapToRouteCalculationResponse(savedItineraire);
    }

    @Transactional
    public OptimizedRouteResponse optimizeRoute(OptimizeRouteRequest request) {
        log.info("Optimizing route with {} destinations", request.getDestinations().size());

        // Utiliser l'algorithme d'optimisation
        List<LocationDTO> optimizedOrder = routeOptimizer.optimizeRoute(
                request.getPointDepart(),
                request.getDestinations(),
                request.getPointRetour()
        );

        // Calculer la distance et durée totales
        double totalDistance = 0.0;
        int totalDuration = 0;

        LocationDTO previous = request.getPointDepart();
        for (LocationDTO location : optimizedOrder) {
            double segmentDistance = distanceCalculator.calculateDistance(
                    previous.getLatitude(), previous.getLongitude(),
                    location.getLatitude(), location.getLongitude()
            );
            totalDistance += segmentDistance;
            totalDuration += (int) ((segmentDistance / averageSpeed) * 60);
            previous = location;
        }

        // Si point de retour défini
        if (request.getPointRetour() != null) {
            double returnDistance = distanceCalculator.calculateDistance(
                    previous.getLatitude(), previous.getLongitude(),
                    request.getPointRetour().getLatitude(), request.getPointRetour().getLongitude()
            );
            totalDistance += returnDistance;
            totalDuration += (int) ((returnDistance / averageSpeed) * 60);
        }

        // Calculer le coût total
        double totalCost = pricingCalculator.calculateCost(totalDistance, TypeCalcul.DISTANCE_COURTE);

        // Construire la description
        String description = String.format(
                "Itinéraire optimisé pour %d destination(s). Distance totale: %.2f km, Durée estimée: %d minutes",
                optimizedOrder.size(), totalDistance, totalDuration
        );

        log.info("Route optimization completed - Distance: {} km, Duration: {} min", totalDistance, totalDuration);

        return OptimizedRouteResponse.builder()
                .ordreOptimise(optimizedOrder)
                .distanceTotale(totalDistance)
                .dureeTotale(totalDuration)
                .coutTotal(totalCost)
                .description(description)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "routes", key = "#id")
    public RouteCalculationResponse getRouteById(Long id) {
        log.info("Fetching route with ID: {}", id);
        Itineraire itineraire = itineraireRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with ID: " + id));

        return mapToRouteCalculationResponse(itineraire);
    }

    @Transactional(readOnly = true)
    public RouteCalculationResponse getRouteByDeliveryId(Long livraisonId) {
        log.info("Fetching route for delivery: {}", livraisonId);
        Itineraire itineraire = itineraireRepository.findByLivraisonId(livraisonId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found for delivery: " + livraisonId));

        return mapToRouteCalculationResponse(itineraire);
    }

    @Transactional(readOnly = true)
    public List<RouteCalculationResponse> getAllRoutes() {
        log.info("Fetching all routes");
        return itineraireRepository.findAll().stream()
                .map(this::mapToRouteCalculationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RouteCalculationResponse> getRoutesByStatus(StatutItineraire statut) {
        log.info("Fetching routes with status: {}", statut);
        return itineraireRepository.findByStatut(statut).stream()
                .map(this::mapToRouteCalculationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "routes", key = "#id")
    public RouteCalculationResponse updateRouteStatus(Long id, StatutItineraire newStatus) {
        log.info("Updating route {} status to {}", id, newStatus);
        Itineraire itineraire = itineraireRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with ID: " + id));

        itineraire.setStatut(newStatus);
        Itineraire updated = itineraireRepository.save(itineraire);

        return mapToRouteCalculationResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "routes", key = "#id")
    public void deleteRoute(Long id) {
        log.info("Deleting route with ID: {}", id);
        if (!itineraireRepository.existsById(id)) {
            throw new RouteNotFoundException("Route not found with ID: " + id);
        }
        itineraireRepository.deleteById(id);
    }

    // ==================== Méthodes privées ====================

    private Etape createEtape(LocationDTO location, int ordre, double distance, int duration) {
        Etape etape = new Etape();
        etape.setOrdre(ordre);
        etape.setLatitude(location.getLatitude());
        etape.setLongitude(location.getLongitude());
        etape.setNomLieu(location.getNom() != null ? location.getNom() : location.getAdresse());
        etape.setDistanceDepuisPrecedent(distance);
        etape.setDureeDepuisPrecedent(duration);
        return etape;
    }

    private RouteCalculationResponse mapToRouteCalculationResponse(Itineraire itineraire) {
        List<EtapeResponse> etapeResponses = itineraire.getEtapes() != null ?
                itineraire.getEtapes().stream()
                        .map(this::mapToEtapeResponse)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        return RouteCalculationResponse.builder()
                .idItineraire(itineraire.getIdItineraire())
                .livraisonId(itineraire.getLivraisonId())
                .typeCalcul(itineraire.getTypeCalcul())
                .statut(itineraire.getStatut())
                .distanceKm(itineraire.getDistanceKm())
                .dureeMinutes(itineraire.getDureeMinutes())
                .coutEstime(itineraire.getCoutEstime())
                .coutCarburant(itineraire.getCoutCarburant())
                .emissionsCo2(itineraire.getEmissionsCo2())
                .vitesseMoyenne(itineraire.getVitesseMoyenne())
                .polyline(itineraire.getPolyline())
                .etapes(etapeResponses)
                .dateCalcul(itineraire.getDateCalcul())
                .notes(itineraire.getNotes())
                .build();
    }

    private EtapeResponse mapToEtapeResponse(Etape etape) {
        return EtapeResponse.builder()
                .idEtape(etape.getIdEtape())
                .ordre(etape.getOrdre())
                .nomLieu(etape.getNomLieu())
                .description(etape.getDescription())
                .typeRoute(etape.getTypeRoute())
                .distanceDepuisPrecedent(etape.getDistanceDepuisPrecedent())
                .dureeDepuisPrecedent(etape.getDureeDepuisPrecedent())
                .instructions(etape.getInstructions())
                .build();
    }
}