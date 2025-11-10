package com.logistics.delivery_service.service;


import com.logistics.delivery_service.dtos.responseDTOs.ColisResponse;
import com.logistics.delivery_service.enums.EtatColis;
import com.logistics.delivery_service.exceptions.ColisNotFoundException;
import com.logistics.delivery_service.model.Colis;
import com.logistics.delivery_service.repository.ColisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ColisService {

    private final ColisRepository colisRepository;

    @Transactional(readOnly = true)
    public ColisResponse getColisById(Long id) {
        log.info("Fetching package with ID: {}", id);
        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ColisNotFoundException("Package not found with ID: " + id));

        return mapToColisResponse(colis);
    }

    @Transactional(readOnly = true)
    public ColisResponse getColisByTrackingCode(String trackingCode) {
        log.info("Fetching package with tracking code: {}", trackingCode);
        Colis colis = colisRepository.findByCodeTracking(trackingCode)
                .orElseThrow(() -> new ColisNotFoundException("Package not found with tracking code: " + trackingCode));

        return mapToColisResponse(colis);
    }

    @Transactional(readOnly = true)
    public List<ColisResponse> getColisByLivraison(Long livraisonId) {
        log.info("Fetching packages for delivery: {}", livraisonId);
        return colisRepository.findByLivraisonIdLivraison(livraisonId).stream()
                .map(this::mapToColisResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ColisResponse updateColisStatus(Long id, EtatColis etat, String notes) {
        log.info("Updating package {} status to {}", id, etat);

        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ColisNotFoundException("Package not found with ID: " + id));

        colis.setEtat(etat);
        if (notes != null) {
            colis.setNotes(notes);
        }

        Colis updatedColis = colisRepository.save(colis);
        log.info("Package status updated successfully");

        return mapToColisResponse(updatedColis);
    }

    @Transactional
    public ColisResponse scanColis(Long id) {
        log.info("Scanning package: {}", id);

        Colis colis = colisRepository.findById(id)
                .orElseThrow(() -> new ColisNotFoundException("Package not found with ID: " + id));

        colis.setDateScan(LocalDateTime.now());

        // Mettre à jour l'état en fonction de l'état actuel
        if (colis.getEtat() == EtatColis.EN_ATTENTE) {
            colis.setEtat(EtatColis.EN_PREPARATION);
        } else if (colis.getEtat() == EtatColis.EN_PREPARATION) {
            colis.setEtat(EtatColis.EN_TRANSIT);
        }

        Colis scannedColis = colisRepository.save(colis);
        log.info("Package scanned successfully");

        return mapToColisResponse(scannedColis);
    }

    private ColisResponse mapToColisResponse(Colis colis) {
        return ColisResponse.builder()
                .idColis(colis.getIdColis())
                .packageId(colis.getPackageId())
                .poids(colis.getPoids())
                .description(colis.getDescription())
                .dimensions(colis.getDimensions())
                .etat(colis.getEtat())
                .codeTracking(colis.getCodeTracking())
                .dateScan(colis.getDateScan())
                .notes(colis.getNotes())
                .createdAt(colis.getCreatedAt())
                .build();
    }
}