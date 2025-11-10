package com.logistics.delivery_service.repository;

import com.logistics.delivery_service.enums.EtatLivraison;
import com.logistics.delivery_service.model.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison , Long> {
    List<Livraison> findByClientId(Long clientId);

    List<Livraison> findByLivreurId(Long livreurId);

    List<Livraison> findByOrderId(Long orderId);

    List<Livraison> findByEtat(EtatLivraison etat);

    List<Livraison> findByEtatIn(List<EtatLivraison> etats);

    List<Livraison> findByLivreurIdAndEtat(Long livreurId, EtatLivraison etat);

    @Query("SELECT l FROM Livraison l WHERE l.etat = :etat AND l.dateLivraisonPrevue < :date")
    List<Livraison> findOverdueLivraisons(@Param("etat") EtatLivraison etat, @Param("date") LocalDateTime date);

    @Query("SELECT l FROM Livraison l WHERE l.livreurId IS NULL AND l.etat = 'EN_ATTENTE'")
    List<Livraison> findUnassignedLivraisons();

    Long countByEtat(EtatLivraison etat);

    Long countByLivreurIdAndEtat(Long livreurId, EtatLivraison etat);

    @Query("SELECT COUNT(l) FROM Livraison l")
    long countLivraisons();

}
