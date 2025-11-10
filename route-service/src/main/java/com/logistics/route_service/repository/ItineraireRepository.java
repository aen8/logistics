package com.logistics.route_service.repository;

import com.logistics.route_service.enums.StatutItineraire;
import com.logistics.route_service.model.Itineraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItineraireRepository extends JpaRepository<Itineraire, Long> {

    Optional<Itineraire> findByLivraisonId(Long livraisonId);

    List<Itineraire> findByStatut(StatutItineraire statut);

    @Query("SELECT i FROM Itineraire i WHERE i.dateCalcul BETWEEN :startDate AND :endDate")
    List<Itineraire> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(i.distanceKm) FROM Itineraire i")
    Double getAverageDistance();

    @Query("SELECT AVG(i.dureeMinutes) FROM Itineraire i")
    Double getAverageDuration();

    Long countByStatut(StatutItineraire statut);
}