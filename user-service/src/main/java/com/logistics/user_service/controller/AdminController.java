package com.logistics.user_service.controller;

import com.logistics.user_service.dtos.AddUserRequest;
import com.logistics.user_service.dtos.RegisterRequest;
import com.logistics.user_service.dtos.response.*;
import com.logistics.user_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ClientService clientService;
    private final LivreurService livreurService;
    private final ManagerService managerService;
    private final AdminService adminService;

// Add user by Admin
    @PostMapping("/users")
    public UserResponse addUser(@RequestBody AddUserRequest request ){

        return adminService.addUserByAdmin(request);
    }
    // ============ Dashboard Statistics ============

    @GetMapping("/dashboard")

    public ResponseEntity<DashboardResponse> getDashboard() {
        DashboardResponse dashboard = adminService.getDashboardStatistics();
        return ResponseEntity.ok(dashboard);
    }


    // ============ User Management ============

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        UserResponse response = adminService.activateUser(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        UserResponse response = adminService.deactivateUser(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ============ Client Management ============

    @GetMapping("/clients")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        ClientResponse client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    // ============ Livreur Management ============

    @GetMapping("/livreurs")
    public ResponseEntity<List<LivreurResponse>> getAllLivreurs() {
        List<LivreurResponse> livreurs = livreurService.getAllLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    @GetMapping("/livreurs/available")
    public ResponseEntity<List<LivreurResponse>> getAvailableLivreurs() {
        List<LivreurResponse> livreurs = livreurService.getAvailableLivreurs();
        return ResponseEntity.ok(livreurs);
    }

    @PutMapping("/livreurs/{id}/disponibilite")
    public ResponseEntity<LivreurResponse> updateLivreurDisponibilite(@PathVariable Long id,
                                                                      @RequestParam Boolean disponibilite) {
        LivreurResponse response = livreurService.updateDisponibilite(id, disponibilite);
        return ResponseEntity.ok(response);
    }

    // ============ Manager Management ============

    @GetMapping("/managers")
    public ResponseEntity<List<ManagerResponse>> getAllManagers() {
        List<ManagerResponse> managers = managerService.getAllManagers();
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/managers/{id}")
    public ResponseEntity<ManagerResponse> getManagerById(@PathVariable Long id) {
        ManagerResponse manager = managerService.getManagerById(id);
        return ResponseEntity.ok(manager);
    }

    @GetMapping("/managers/region/{region}")
    public ResponseEntity<List<ManagerResponse>> getManagersByRegion(@PathVariable String region) {
        List<ManagerResponse> managers = managerService.getManagersByRegion(region);
        return ResponseEntity.ok(managers);
    }

    @PutMapping("/managers/{id}/region")
    public ResponseEntity<ManagerResponse> updateManagerRegion(@PathVariable Long id,
                                                               @RequestParam String region) {
        ManagerResponse response = managerService.updateRegion(id, region);
        return ResponseEntity.ok(response);
    }

    // ============ Reports ============

    @GetMapping("/reports/users-by-role")
    public ResponseEntity<Map<String, Long>> getUsersByRole() {
        Map<String, Long> stats = adminService.getUsersByRole();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/reports/active-users")
    public ResponseEntity<Map<String, Object>> getActiveUsersReport() {
        Map<String, Object> report = adminService.getActiveUsersReport();
        return ResponseEntity.ok(report);
    }
}