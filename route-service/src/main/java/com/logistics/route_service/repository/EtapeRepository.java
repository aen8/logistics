package com.logistics.route_service.repository;

import com.logistics.route_service.model.Etape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EtapeRepository extends JpaRepository<Etape, Long> {

    List<Etape> findByItineraireIdItineraireOrderByOrdre(Long itineraireId);
}