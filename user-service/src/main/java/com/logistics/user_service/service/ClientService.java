package com.logistics.user_service.service;

import com.logistics.user_service.dtos.AdresseRequest;
import com.logistics.user_service.dtos.response.AdresseResponse;
import com.logistics.user_service.dtos.response.ClientResponse;
import com.logistics.user_service.dtos.response.OrderResponse;
import com.logistics.user_service.exceptions.UserNotFoundException;
import com.logistics.user_service.model.Adresse;
import com.logistics.user_service.model.Client;
import com.logistics.user_service.repository.AdresseRepository;
import com.logistics.user_service.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final AdresseRepository adresseRepository;

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        log.info("Fetching client with ID: {}", id);
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Client not found with ID: " + id));

        return mapToClientResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        log.info("Fetching all clients");
        return clientRepository.findAll().stream()
                .map(this::mapToClientResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdresseResponse addAdresse(Long clientId, AdresseRequest request) {
        log.info("Adding address for client ID: {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new UserNotFoundException("Client not found with ID: " + clientId));

        Adresse adresse = new Adresse();
        adresse.setRue(request.getRue());
        adresse.setVille(request.getVille());
        adresse.setCodePostal(request.getCodePostal());
        adresse.setPays(request.getPays());
        adresse.setLatitude(request.getLatitude());
        adresse.setLongitude(request.getLongitude());
        adresse.setEstPrincipale(request.getEstPrincipale());

        client.addAdresse(adresse);
        clientRepository.save(client);

        log.info("Address added successfully for client ID: {}", clientId);
        return mapToAdresseResponse(adresse);
    }

    @Transactional(readOnly = true)
    public List<AdresseResponse> getClientAdresses(Long clientId) {
        log.info("Fetching addresses for client ID: {}", clientId);
        return adresseRepository.findByClientIdUser(clientId).stream()
                .map(this::mapToAdresseResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getClientOrders(Long clientId) {
        log.info("Fetching orders for client ID: {}", clientId);

        // Vérifier que le client existe
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new UserNotFoundException("Client not found with ID: " + clientId));

        // On va  Appeler order-service via Feign pour récupérer les commandes
        // Cette méthode sera complétée quand order-service sera implémenté

        return new ArrayList<>();
    }

    private ClientResponse mapToClientResponse(Client client) {
        List<AdresseResponse> adresses = client.getAdresses().stream()
                .map(this::mapToAdresseResponse)
                .collect(Collectors.toList());

        return ClientResponse.builder()
                .idUser(client.getIdUser())
                .email(client.getEmail())
                .nom(client.getNom())
                .telephone(client.getTelephone())
                .active(client.getActive())
                .createdAt(client.getCreatedAt())
                .adresses(adresses)
                .build();
    }

    private AdresseResponse mapToAdresseResponse(Adresse adresse) {
        return AdresseResponse.builder()
                .idAdresse(adresse.getIdAdresse())
                .rue(adresse.getRue())
                .ville(adresse.getVille())
                .codePostal(adresse.getCodePostal())
                .pays(adresse.getPays())
                .latitude(adresse.getLatitude())
                .longitude(adresse.getLongitude())
                .estPrincipale(adresse.getEstPrincipale())
                .build();
    }
}