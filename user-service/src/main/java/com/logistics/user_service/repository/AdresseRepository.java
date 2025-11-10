package com.logistics.user_service.repository;

import com.logistics.user_service.model.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdresseRepository extends JpaRepository<Adresse , Long> {
    List<Adresse> findByClientIdUser(Long clientId);
}
