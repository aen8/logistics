package com.logistics.user_service.controller;

import com.logistics.user_service.dtos.AdresseRequest;
import com.logistics.user_service.dtos.response.AdresseResponse;
import com.logistics.user_service.dtos.response.ClientResponse;
import com.logistics.user_service.dtos.response.OrderResponse;
import com.logistics.user_service.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PostMapping("/{id}/adresses")
    @PreAuthorize("hasRole('CLIENT') and @userSecurity.isOwner(#id)")
    public ResponseEntity<AdresseResponse> addAdresse(@PathVariable Long id,
                                                      @Valid @RequestBody AdresseRequest request) {
        AdresseResponse response = clientService.addAdresse(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/adresses")
    public ResponseEntity<List<AdresseResponse>> getClientAdresses(@PathVariable Long id) {
        List<AdresseResponse> adresses = clientService.getClientAdresses(id);
        return ResponseEntity.ok(adresses);
    }
    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('CLIENT') and @userSecurity.isOwner(#id)")
    public ResponseEntity<List<OrderResponse>> getClientOrders(@PathVariable Long id) {
        List<OrderResponse> orders = clientService.getClientOrders(id);
        return ResponseEntity.ok(orders);
    }
}