package com.logistics.warehouse_service.repository;


import com.logistics.warehouse_service.enums.StatutEntrepot;
import com.logistics.warehouse_service.enums.TypeEntrepot;
import com.logistics.warehouse_service.model.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepotRepository extends JpaRepository<Entrepot, Long> {

    Optional<Entrepot> findByCode(String code);

    List<Entrepot> findByStatut(StatutEntrepot statut);

    List<Entrepot> findByType(TypeEntrepot type);

    List<Entrepot> findByAdresseVille(String ville);

    @Query("SELECT e FROM Entrepot e WHERE e.statut = 'ACTIF' ORDER BY e.capaciteActuelle ASC")
    List<Entrepot> findActiveWarehousesOrderByCapacity();

    @Query("SELECT e FROM Entrepot e WHERE e.statut = 'ACTIF' AND (e.capaciteMax - e.capaciteActuelle) >= :requiredCapacity")
    List<Entrepot> findAvailableWarehousesWithCapacity(@Param("requiredCapacity") int requiredCapacity);

    @Query(value = "SELECT * FROM entrepots e WHERE e.statut = 'ACTIF' " +
            "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(e.latitude)) * " +
            "cos(radians(e.longitude) - radians(:lon)) + sin(radians(:lat)) * " +
            "sin(radians(e.latitude)))) ASC LIMIT 1", nativeQuery = true)
    Optional<Entrepot> findNearestWarehouse(@Param("lat") Double latitude, @Param("lon") Double longitude);

    @Query("SELECT COUNT(e) FROM Entrepot e WHERE e.statut = :statut")
    Long countByStatut(@Param("statut") StatutEntrepot statut);
}