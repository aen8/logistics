package com.logistics.warehouse_service.service;

import com.logistics.warehouse_service.dtos.AdresseDTO;
import com.logistics.warehouse_service.dtos.requestDTOs.EntrepotRequest;
import com.logistics.warehouse_service.dtos.requestDTOs.UpdateEntrepotRequest;
import com.logistics.warehouse_service.dtos.responseDTOs.EntrepotResponse;
import com.logistics.warehouse_service.dtos.responseDTOs.EntrepotStatsResponse;
import com.logistics.warehouse_service.enums.StatutEntrepot;
import com.logistics.warehouse_service.enums.TypeEntrepot;
import com.logistics.warehouse_service.enums.TypeStock;
import com.logistics.warehouse_service.exceptions.EntrepotNotFoundException;
import com.logistics.warehouse_service.exceptions.WarehouseCodeAlreadyExistsException;
import com.logistics.warehouse_service.model.Entrepot;
import com.logistics.warehouse_service.repository.EntrepotRepository;
import com.logistics.warehouse_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntrepotService {

    private final EntrepotRepository entrepotRepository;
    private final StockRepository stockRepository;

    @Transactional
    public EntrepotResponse createEntrepot(EntrepotRequest request) {
        log.info("Creating warehouse with code: {}", request.getCode());

        // Vérifier si le code existe déjà
        if (entrepotRepository.findByCode(request.getCode()).isPresent()) {
            throw new WarehouseCodeAlreadyExistsException("Warehouse code already exists: " + request.getCode());
        }

        Entrepot entrepot = new Entrepot();
        entrepot.setCode(request.getCode());
        entrepot.setNom(request.getNom());
        entrepot.setType(request.getType());
        entrepot.setStatut(StatutEntrepot.ACTIF);

        // Adresse
        AdresseDTO adresse = request.getAdresse();
        entrepot.setAdresseRue(adresse.getRue());
        entrepot.setAdresseVille(adresse.getVille());
        entrepot.setAdresseCodePostal(adresse.getCodePostal());
        entrepot.setAdressePays(adresse.getPays());
        entrepot.setLatitude(adresse.getLatitude());
        entrepot.setLongitude(adresse.getLongitude());

        // Autres informations
        entrepot.setCapaciteMax(request.getCapaciteMax());
        entrepot.setCapaciteActuelle(0);
        entrepot.setSuperficieM2(request.getSuperficieM2());
        entrepot.setTelephone(request.getTelephone());
        entrepot.setEmail(request.getEmail());
        entrepot.setResponsableNom(request.getResponsableNom());
        entrepot.setResponsableTelephone(request.getResponsableTelephone());
        entrepot.setHorairesOuverture(request.getHorairesOuverture());
        entrepot.setDescription(request.getDescription());

        Entrepot savedEntrepot = entrepotRepository.save(entrepot);
        log.info("Warehouse created with ID: {}", savedEntrepot.getIdEntrepot());

        return mapToEntrepotResponse(savedEntrepot);
    }

    @Transactional(readOnly = true)
    public EntrepotResponse getEntrepotById(Long id) {
        log.info("Fetching warehouse with ID: {}", id);
        Entrepot entrepot = entrepotRepository.findById(id)
                .orElseThrow(() -> new EntrepotNotFoundException("Warehouse not found with ID: " + id));

        return mapToEntrepotResponse(entrepot);
    }



    @Transactional(readOnly = true)
    public EntrepotResponse getEntrepotByCode(String code) {
        log.info("Fetching warehouse with code: {}", code);
        Entrepot entrepot = entrepotRepository.findByCode(code)
                .orElseThrow(() -> new EntrepotNotFoundException("Warehouse not found with code: " + code));

        return mapToEntrepotResponse(entrepot);
    }

    @Transactional(readOnly = true)
    public List<EntrepotResponse> getAllEntrepots() {
        log.info("Fetching all warehouses");
        return entrepotRepository.findAll().stream()
                .map(this::mapToEntrepotResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntrepotResponse> getEntrepotsByStatus(StatutEntrepot statut) {
        log.info("Fetching warehouses with status: {}", statut);
        return entrepotRepository.findByStatut(statut).stream()
                .map(this::mapToEntrepotResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntrepotResponse> getEntrepotsByType(TypeEntrepot type) {
        log.info("Fetching warehouses with type: {}", type);
        return entrepotRepository.findByType(type).stream()
                .map(this::mapToEntrepotResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntrepotResponse> getEntrepotsByVille(String ville) {
        log.info("Fetching warehouses in city: {}", ville);
        return entrepotRepository.findByAdresseVille(ville).stream()
                .map(this::mapToEntrepotResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntrepotResponse> getAvailableWarehouses(int requiredCapacity) {
        log.info("Fetching warehouses with capacity >= {}", requiredCapacity);
        return entrepotRepository.findAvailableWarehousesWithCapacity(requiredCapacity).stream()
                .map(this::mapToEntrepotResponse)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public EntrepotResponse getNearestWarehouse(Double latitude, Double longitude) {
        log.info("Finding nearest warehouse to coordinates: {}, {}", latitude, longitude);
        Entrepot entrepot = entrepotRepository.findNearestWarehouse(latitude, longitude)
                .orElseThrow(() -> new EntrepotNotFoundException("No warehouse found"));

        return mapToEntrepotResponse(entrepot);
    }

    @Transactional
    public EntrepotResponse updateEntrepot(Long id, UpdateEntrepotRequest request) {
        log.info("Updating warehouse with ID: {}", id);
        Entrepot entrepot = entrepotRepository.findById(id)
                .orElseThrow(() -> new EntrepotNotFoundException("Warehouse not found with ID: " + id));

        if (request.getNom() != null) {
            entrepot.setNom(request.getNom());
        }
        if (request.getStatut() != null) {
            entrepot.setStatut(request.getStatut());
        }
        if (request.getCapaciteMax() != null) {
            entrepot.setCapaciteMax(request.getCapaciteMax());
        }
        if (request.getTelephone() != null) {
            entrepot.setTelephone(request.getTelephone());
        }
        if (request.getEmail() != null) {
            entrepot.setEmail(request.getEmail());
        }
        if (request.getResponsableNom() != null) {
            entrepot.setResponsableNom(request.getResponsableNom());
        }
        if (request.getResponsableTelephone() != null) {
            entrepot.setResponsableTelephone(request.getResponsableTelephone());
        }
        if (request.getHorairesOuverture() != null) {
            entrepot.setHorairesOuverture(request.getHorairesOuverture());
        }
        if (request.getDescription() != null) {
            entrepot.setDescription(request.getDescription());
        }

        Entrepot updatedEntrepot = entrepotRepository.save(entrepot);
        log.info("Warehouse updated successfully");

        return mapToEntrepotResponse(updatedEntrepot);
    }

    @Transactional
    public void deleteEntrepot(Long id) {
        log.info("Deleting warehouse with ID: {}", id);
        Entrepot entrepot = entrepotRepository.findById(id)
                .orElseThrow(() -> new EntrepotNotFoundException("Warehouse not found with ID: " + id));

        entrepot.setStatut(StatutEntrepot.FERME);
        entrepotRepository.save(entrepot);
        log.info("Warehouse marked as closed");
    }

    @Transactional(readOnly = true)
    public AdresseDTO getWarehouseAddress(Long id) {
        log.info("Fetching address for warehouse: {}", id);
        Entrepot entrepot = entrepotRepository.findById(id)
                .orElseThrow(() -> new EntrepotNotFoundException("Warehouse not found with ID: " + id));

        return AdresseDTO.builder()
                .rue(entrepot.getAdresseRue())
                .ville(entrepot.getAdresseVille())
                .codePostal(entrepot.getAdresseCodePostal())
                .pays(entrepot.getAdressePays())
                .latitude(entrepot.getLatitude())
                .longitude(entrepot.getLongitude())
                .build();
    }

    @Transactional(readOnly = true)
    public EntrepotStatsResponse getWarehouseStats(Long id) {
        log.info("Fetching statistics for warehouse: {}", id);
        Entrepot entrepot = entrepotRepository.findById(id)
                .orElseThrow(() -> new EntrepotNotFoundException("Warehouse not found with ID: " + id));

        Long nombreEntrees = stockRepository.countByEntrepotAndType(id, TypeStock.ENTREE);
        Long nombreSorties = stockRepository.countByEntrepotAndType(id,TypeStock.SORTIE);
        Long nombreTransferts = stockRepository.countByEntrepotAndType(id, TypeStock.TRANSFERT);

        return EntrepotStatsResponse.builder()
                .entrepotId(entrepot.getIdEntrepot())
                .entrepotNom(entrepot.getNom())
                .capaciteMax(entrepot.getCapaciteMax())
                .capaciteActuelle(entrepot.getCapaciteActuelle())
                .capaciteDisponible(entrepot.getCapaciteDisponible())
                .tauxOccupation(entrepot.getTauxOccupation())
                .nombreEntrees(nombreEntrees)
                .nombreSorties(nombreSorties)
                .nombreTransferts(nombreTransferts)
                .build();
    }

    private EntrepotResponse mapToEntrepotResponse(Entrepot entrepot) {
        AdresseDTO adresse = AdresseDTO.builder()
                .rue(entrepot.getAdresseRue())
                .ville(entrepot.getAdresseVille())
                .codePostal(entrepot.getAdresseCodePostal())
                .pays(entrepot.getAdressePays())
                .latitude(entrepot.getLatitude())
                .longitude(entrepot.getLongitude())
                .build();

        return EntrepotResponse.builder()
                .idEntrepot(entrepot.getIdEntrepot())
                .code(entrepot.getCode())
                .nom(entrepot.getNom())
                .type(entrepot.getType())
                .statut(entrepot.getStatut())
                .adresse(adresse)
                .capaciteMax(entrepot.getCapaciteMax())
                .capaciteActuelle(entrepot.getCapaciteActuelle())
                .capaciteDisponible(entrepot.getCapaciteDisponible())
                .tauxOccupation(entrepot.getTauxOccupation())
                .superficieM2(entrepot.getSuperficieM2())
                .telephone(entrepot.getTelephone())
                .email(entrepot.getEmail())
                .responsableNom(entrepot.getResponsableNom())
                .responsableTelephone(entrepot.getResponsableTelephone())
                .horairesOuverture(entrepot.getHorairesOuverture())
                .description(entrepot.getDescription())
                .createdAt(entrepot.getCreatedAt())
                .updatedAt(entrepot.getUpdatedAt())
                .build();
    }
}
