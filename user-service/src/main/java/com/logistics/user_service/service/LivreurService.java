package com.logistics.user_service.service;

import com.logistics.user_service.dtos.UpdateLivreurPositionRequest;
import com.logistics.user_service.dtos.response.LivreurResponse;
import com.logistics.user_service.exceptions.UserNotFoundException;
import com.logistics.user_service.model.Livreur;
import com.logistics.user_service.repository.LivreurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivreurService {

    private final LivreurRepository livreurRepository;

    @Transactional(readOnly = true)
    public LivreurResponse getLivreurById(Long id) {
        log.info("Fetching livreur with ID: {}", id);
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Livreur not found with ID: " + id));

        return mapToLivreurResponse(livreur);
    }

    @Transactional(readOnly = true)
    public List<LivreurResponse> getAllLivreurs() {
        log.info("Fetching all livreurs");
        return livreurRepository.findAll().stream()
                .map(this::mapToLivreurResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LivreurResponse> getAvailableLivreurs() {
        log.info("Fetching available livreurs");
        return livreurRepository.findByDisponibiliteAndActiveTrue(true).stream()
                .map(this::mapToLivreurResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LivreurResponse updatePosition(Long id, UpdateLivreurPositionRequest request) {
        log.info("Updating position for livreur ID: {}", id);
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Livreur not found with ID: " + id));

        livreur.setLatitudeActuelle(request.getLatitude());
        livreur.setLongitudeActuelle(request.getLongitude());

        Livreur updatedLivreur = livreurRepository.save(livreur);
        log.info("Position updated successfully for livreur ID: {}", id);

        return mapToLivreurResponse(updatedLivreur);
    }

    @Transactional
    public LivreurResponse updateDisponibilite(Long id, Boolean disponibilite) {
        log.info("Updating disponibilite for livreur ID: {} to {}", id, disponibilite);
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Livreur not found with ID: " + id));

        livreur.setDisponibilite(disponibilite);

        Livreur updatedLivreur = livreurRepository.save(livreur);
        log.info("Disponibilite updated successfully for livreur ID: {}", id);

        return mapToLivreurResponse(updatedLivreur);
    }

    @Transactional
    public void incrementLivraisonsEffectuees(Long id) {
        log.info("Incrementing livraisons effectuées for livreur ID: {}", id);
        Livreur livreur = livreurRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Livreur not found with ID: " + id));

        livreur.setNombreLivraisonsEffectuees(livreur.getNombreLivraisonsEffectuees() + 1);
        livreurRepository.save(livreur);
    }

    private LivreurResponse mapToLivreurResponse(Livreur livreur) {
        return LivreurResponse.builder()
                .idUser(livreur.getIdUser())
                .email(livreur.getEmail())
                .nom(livreur.getNom())
                .telephone(livreur.getTelephone())
                .disponibilite(livreur.getDisponibilite())
                .vehiculeId(livreur.getVehiculeId())
                .latitudeActuelle(livreur.getLatitudeActuelle())
                .longitudeActuelle(livreur.getLongitudeActuelle())
                .nombreLivraisonsEffectuees(livreur.getNombreLivraisonsEffectuees())
                .noteMoyenne(livreur.getNoteMoyenne())
                .active(livreur.getActive())
                .createdAt(livreur.getCreatedAt())
                .build();
    }
}