package com.logistics.route_service.service;

import com.logistics.route_service.dtos.LocationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteOptimizer {

    private final DistanceCalculator distanceCalculator;

    /**
     * Optimise l'ordre des destinations pour minimiser la distance totale
     * Utilise un algorithme du plus proche voisin (Nearest Neighbor)
     *
     * @param pointDepart Point de départ
     * @param destinations Liste des destinations à visiter
     * @param pointRetour Point de retour (optionnel)
     * @return Liste ordonnée des destinations
     */
    public List<LocationDTO> optimizeRoute(LocationDTO pointDepart,
                                           List<LocationDTO> destinations,
                                           LocationDTO pointRetour) {

        if (destinations == null || destinations.isEmpty()) {
            log.warn("No destinations to optimize");
            return new ArrayList<>();
        }

        log.info("Optimizing route for {} destinations", destinations.size());

        // Pour un petit nombre de destinations (< 10), utiliser l'algorithme du plus proche voisin
        if (destinations.size() < 10) {
            return nearestNeighborAlgorithm(pointDepart, new ArrayList<>(destinations));
        } else {
            // Pour un grand nombre de destinations, utiliser une approche heuristique plus rapide
            return nearestNeighborAlgorithm(pointDepart, new ArrayList<>(destinations));
        }
    }

    /**
     * Algorithme du plus proche voisin
     * Complexité: O(n²)
     */
    private List<LocationDTO> nearestNeighborAlgorithm(LocationDTO start, List<LocationDTO> unvisited) {
        List<LocationDTO> optimizedRoute = new ArrayList<>();
        LocationDTO current = start;

        while (!unvisited.isEmpty()) {
            LocationDTO nearest = findNearestLocation(current, unvisited);
            optimizedRoute.add(nearest);
            unvisited.remove(nearest);
            current = nearest;
        }

        log.debug("Route optimized with {} stops", optimizedRoute.size());
        return optimizedRoute;
    }

    /**
     * Trouve la location la plus proche parmi une liste
     */
    private LocationDTO findNearestLocation(LocationDTO from, List<LocationDTO> locations) {
        LocationDTO nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (LocationDTO location : locations) {
            double distance = distanceCalculator.calculateDistance(
                    from.getLatitude(), from.getLongitude(),
                    location.getLatitude(), location.getLongitude()
            );

            if (distance < minDistance) {
                minDistance = distance;
                nearest = location;
            }
        }

        return nearest;
    }

    /**
     * Calcule la distance totale d'un itinéraire
     */
    public double calculateTotalRouteDistance(LocationDTO start, List<LocationDTO> route, LocationDTO end) {
        double totalDistance = 0.0;
        LocationDTO current = start;

        for (LocationDTO location : route) {
            totalDistance += distanceCalculator.calculateDistance(
                    current.getLatitude(), current.getLongitude(),
                    location.getLatitude(), location.getLongitude()
            );
            current = location;
        }

        // Ajouter la distance de retour si définie
        if (end != null) {
            totalDistance += distanceCalculator.calculateDistance(
                    current.getLatitude(), current.getLongitude(),
                    end.getLatitude(), end.getLongitude()
            );
        }

        return totalDistance;
    }

    /**
     * Optimisation 2-opt pour améliorer une route existante
     * Essaie d'inverser des segments de route pour réduire la distance totale
     */
    public List<LocationDTO> twoOptOptimization(LocationDTO start, List<LocationDTO> route) {
        List<LocationDTO> bestRoute = new ArrayList<>(route);
        boolean improvement = true;

        while (improvement) {
            improvement = false;
            double bestDistance = calculateTotalRouteDistance(start, bestRoute, null);

            for (int i = 0; i < bestRoute.size() - 1; i++) {
                for (int j = i + 1; j < bestRoute.size(); j++) {
                    // Créer une nouvelle route en inversant le segment [i, j]
                    List<LocationDTO> newRoute = twoOptSwap(bestRoute, i, j);
                    double newDistance = calculateTotalRouteDistance(start, newRoute, null);

                    if (newDistance < bestDistance) {
                        bestRoute = newRoute;
                        bestDistance = newDistance;
                        improvement = true;
                    }
                }
            }
        }

        log.debug("2-opt optimization completed");
        return bestRoute;
    }

    /**
     * Effectue un échange 2-opt
     */
    private List<LocationDTO> twoOptSwap(List<LocationDTO> route, int i, int j) {
        List<LocationDTO> newRoute = new ArrayList<>();

        // Ajouter les éléments avant i
        newRoute.addAll(route.subList(0, i));

        // Inverser le segment [i, j]
        List<LocationDTO> reversed = new ArrayList<>(route.subList(i, j + 1));
        java.util.Collections.reverse(reversed);
        newRoute.addAll(reversed);

        // Ajouter les éléments après j
        if (j + 1 < route.size()) {
            newRoute.addAll(route.subList(j + 1, route.size()));
        }

        return newRoute;
    }
}