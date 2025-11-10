package com.logistics.route_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calcule la distance entre deux points géographiques en utilisant la formule de Haversine
     *
     * @param lat1 Latitude du point 1 en degrés
     * @param lon1 Longitude du point 1 en degrés
     * @param lat2 Latitude du point 2 en degrés
     * @param lon2 Longitude du point 2 en degrés
     * @return Distance en kilomètres
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convertir les degrés en radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Différences
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        // Formule de Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        log.debug("Distance calculated: {} km between ({}, {}) and ({}, {})",
                distance, lat1, lon1, lat2, lon2);

        return Math.round(distance * 100.0) / 100.0; // Arrondir à 2 décimales
    }

    /**
     * Calcule la distance totale d'un itinéraire avec plusieurs points
     */
    public double calculateTotalDistance(double[][] coordinates) {
        if (coordinates == null || coordinates.length < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        for (int i = 0; i < coordinates.length - 1; i++) {
            totalDistance += calculateDistance(
                    coordinates[i][0], coordinates[i][1],
                    coordinates[i + 1][0], coordinates[i + 1][1]
            );
        }

        return totalDistance;
    }
}