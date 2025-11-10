package com.logistics.delivery_service.repository;


import com.logistics.delivery_service.enums.EtatColis;
import com.logistics.delivery_service.model.Colis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ColisRepository extends JpaRepository<Colis, Long> {

    List<Colis> findByLivraisonIdLivraison(Long livraisonId);

    Optional<Colis> findByCodeTracking(String codeTracking);

    List<Colis> findByEtat(EtatColis etat);

    List<Colis> findByPackageId(Long packageId);
}