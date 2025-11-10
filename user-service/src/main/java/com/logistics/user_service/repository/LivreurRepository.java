package com.logistics.user_service.repository;

import com.logistics.user_service.model.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivreurRepository extends JpaRepository<Livreur , Long> {
    List<Livreur> findByDisponibiliteAndActiveTrue(Boolean disponibilite);
    Optional<Livreur> findByEmail(String email);
    List<Livreur> findByDisponibilite(Boolean disponibilite);
    Long countByDisponibiliteAndActiveTrue(Boolean disponibilite);
}
