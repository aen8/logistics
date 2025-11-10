package com.logistics.route_service.service;

import com.logistics.route_service.enums.TypeCalcul;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PricingCalculator {

    @Value("${pricing.base-rate-per-km:2.5}")
    private Double baseRatePerKm;

    @Value("${pricing.fuel-price-per-liter:15.0}")
    private Double fuelPricePerLiter; // En MAD

    @Value("${pricing.fuel-consumption-per-100km:8.0}")
    private Double fuelConsumptionPer100Km; // En litres

    @Value("${pricing.co2-emission-per-km:0.12}")
    private Double co2EmissionPerKm; // En kg

    @Value("${pricing.eco-rate-multiplier:0.8}")
    private Double ecoRateMultiplier;

    @Value("${pricing.express-rate-multiplier:1.3}")
    private Double expressRateMultiplier;

    /**
     * Calcule le coût total d'un itinéraire
     */
    public double calculateCost(double distanceKm, TypeCalcul typeCalcul) {
        double baseCost = distanceKm * baseRatePerKm;

        // Appliquer les multiplicateurs selon le type de calcul
        double finalCost = switch (typeCalcul) {
            case DISTANCE_COURTE -> baseCost;
            case TEMPS_RAPIDE -> baseCost * expressRateMultiplier;
            case COUT_OPTIMAL -> baseCost * 0.9; // Réduction pour coût optimal
            case ECO -> baseCost * ecoRateMultiplier;
            default -> baseCost;
        };

        log.debug("Cost calculated: {} MAD for {} km with type {}", finalCost, distanceKm, typeCalcul);
        return Math.round(finalCost * 100.0) / 100.0;
    }

    /**
     * Calcule le coût du carburant
     */
    public double calculateFuelCost(double distanceKm) {
        double litersConsumed = (distanceKm / 100.0) * fuelConsumptionPer100Km;
        double fuelCost = litersConsumed * fuelPricePerLiter;

        log.debug("Fuel cost: {} MAD for {} km ({} liters)", fuelCost, distanceKm, litersConsumed);
        return Math.round(fuelCost * 100.0) / 100.0;
    }

    /**
     * Calcule les émissions de CO2
     */
    public double calculateCO2Emissions(double distanceKm) {
        double emissions = distanceKm * co2EmissionPerKm;

        log.debug("CO2 emissions: {} kg for {} km", emissions, distanceKm);
        return Math.round(emissions * 100.0) / 100.0;
    }

    /**
     * Calcule le coût par kilomètre
     */
    public double calculateCostPerKm(double totalCost, double totalDistance) {
        if (totalDistance == 0) {
            return 0.0;
        }
        return Math.round((totalCost / totalDistance) * 100.0) / 100.0;
    }
}