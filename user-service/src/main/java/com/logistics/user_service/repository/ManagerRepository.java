package com.logistics.user_service.repository;

import com.logistics.user_service.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager , Long> {
    Optional<Manager> findByEmail(String email);
    List<Manager> findByRegion(String region);
    List<Manager> findByActiveTrue();
    List<Manager> findByRegionAndActiveTrue(String region);
}
