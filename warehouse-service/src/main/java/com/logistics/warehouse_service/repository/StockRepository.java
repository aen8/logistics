package com.logistics.warehouse_service.repository;

import com.logistics.warehouse_service.enums.TypeStock;
import com.logistics.warehouse_service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByEntrepotIdEntrepot(Long entrepotId);

    List<Stock> findByColisId(Long colisId);

    List<Stock> findByType(TypeStock type);

    List<Stock> findByEntrepotIdEntrepotAndType(Long entrepotId, TypeStock type);

    @Query("SELECT s FROM Stock s WHERE s.dateMouvement BETWEEN :startDate AND :endDate")
    List<Stock> findByDateMouvementBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Stock s WHERE s.entrepot.idEntrepot = :entrepotId " +
            "AND s.dateMouvement BETWEEN :startDate AND :endDate")
    List<Stock> findByEntrepotAndDateRange(@Param("entrepotId") Long entrepotId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Stock s WHERE s.entrepot.idEntrepot = :entrepotId AND s.type = :type")
    Long countByEntrepotAndType(@Param("entrepotId") Long entrepotId, @Param("type") TypeStock type);
}