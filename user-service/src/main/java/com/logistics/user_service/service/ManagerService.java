package com.logistics.user_service.service;

import com.logistics.user_service.client.DeliveryServiceClient;
import com.logistics.user_service.dtos.response.ManagerDashboardResponse;
import com.logistics.user_service.dtos.response.ManagerResponse;
import com.logistics.user_service.exceptions.UserNotFoundException;
import com.logistics.user_service.model.Manager;
import com.logistics.user_service.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final DeliveryServiceClient deliveryServiceClient ;
    //===========> GET THE MANAGER USING THE ID
    @Transactional(readOnly = true)
    public ManagerResponse getManagerById(Long id) {
        log.info("Fetching manager with ID: {}", id);
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with ID: " + id));

        return mapToManagerResponse(manager);
    }
    //=============> GET THE MANAGER BASED ON THE EMAIL ADDRESS
    @Transactional(readOnly = true)
    public ManagerResponse getManagerByEmail(String email) {
        log.info("Fetching manager with email: {}", email);
        Manager manager = managerRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with email: " + email));

        return mapToManagerResponse(manager);
    }
    //=============> GET ALL THE MANAGERS METHOD
    @Transactional(readOnly = true)
    public List<ManagerResponse> getAllManagers() {
        log.info("Fetching all managers");
        return managerRepository.findAll().stream()
                .map(this::mapToManagerResponse)
                .collect(Collectors.toList());
    }
    //===============> GET THE MANAGER BASED / RELATED TO A SPECIFIC REGION
    @Transactional(readOnly = true)
    public List<ManagerResponse> getManagersByRegion(String region) {
        log.info("Fetching managers for region: {}", region);
        return managerRepository.findByRegion(region).stream()
                .map(this::mapToManagerResponse)
                .collect(Collectors.toList());
    }
    //=============> RETURN JUST THE ACTIVE MANAGERS
    @Transactional(readOnly = true)
    public List<ManagerResponse> getActiveManagers() {
        log.info("Fetching active managers");
        return managerRepository.findByActiveTrue().stream()
                .map(this::mapToManagerResponse)
                .collect(Collectors.toList());
    }
    //=============> UPDATE THE REGION
    @Transactional
    public ManagerResponse updateRegion(Long id, String region) {
        log.info("Updating region for manager ID: {} to {}", id, region);
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with ID: " + id));

        manager.setRegion(region);
        Manager updatedManager = managerRepository.save(manager);

        log.info("Region updated successfully for manager ID: {}", id);
        return mapToManagerResponse(updatedManager);
    }
    //=============> UPDATE THE TIMEZONE
    @Transactional
    public ManagerResponse updateTeamSize(Long id, Integer teamSize) {
        log.info("Updating team size for manager ID: {} to {}", id, teamSize);
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with ID: " + id));

        if (teamSize < 0) {
            throw new IllegalArgumentException("Team size cannot be negative");
        }

        manager.setEquipeTaille(teamSize);
        Manager updatedManager = managerRepository.save(manager);

        log.info("Team size updated successfully for manager ID: {}", id);
        return mapToManagerResponse(updatedManager);
    }

    @Transactional
    public void incrementTeamSize(Long managerId) {
        log.info("Incrementing team size for manager ID: {}", managerId);
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with ID: " + managerId));

        manager.setEquipeTaille(manager.getEquipeTaille() + 1);
        managerRepository.save(manager);

        log.info("Team size incremented for manager ID: {}", managerId);
    }

    @Transactional
    public void decrementTeamSize(Long managerId) {
        log.info("Decrementing team size for manager ID: {}", managerId);
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with ID: " + managerId));

        if (manager.getEquipeTaille() > 0) {
            manager.setEquipeTaille(manager.getEquipeTaille() - 1);
            managerRepository.save(manager);
            log.info("Team size decremented for manager ID: {}", managerId);
        } else {
            log.warn("Cannot decrement team size below 0 for manager ID: {}", managerId);
        }
    }
    @Transactional(readOnly = true)
    public ManagerDashboardResponse getManagerDashboard(Long managerId) {
        log.info("Fetching dashboard for manager ID: {}", managerId);
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException("Manager not found with ID: " + managerId));

        // Ces données viendront des autres services via Feign
        // Pour l'instant, on retourne des valeurs par défaut
        return ManagerDashboardResponse.builder()
                .managerId(manager.getIdUser())
                .managerName(manager.getNom())
                .region(manager.getRegion())
                .teamSize(manager.getEquipeTaille())
                .totalLivraisonsInRegion(deliveryServiceClient.getHowManyLivraison()) // À récupérer du delivery-service
                .pendingLivraisons(0L)
                .completedLivraisons(0L)
                .availableLivreurs(0L)
                .averageDeliveryTime(0.0)
                .build();
    }

    private ManagerResponse mapToManagerResponse(Manager manager) {
        return ManagerResponse.builder()
                .idUser(manager.getIdUser())
                .email(manager.getEmail())
                .nom(manager.getNom())
                .telephone(manager.getTelephone())
                .region(manager.getRegion())
                .equipeTaille(manager.getEquipeTaille())
                .active(manager.getActive())
                .createdAt(manager.getCreatedAt())
                .build();
    }
}