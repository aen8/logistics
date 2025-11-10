package com.logistics.user_service.controller;


import com.logistics.user_service.dtos.AddUserRequest;
import com.logistics.user_service.dtos.response.ManagerDashboardResponse;
import com.logistics.user_service.dtos.response.ManagerResponse;
import com.logistics.user_service.dtos.response.UserResponse;
import com.logistics.user_service.service.AdminService;
import com.logistics.user_service.service.AuthService;
import com.logistics.user_service.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final AdminService adminService;

  //========Add user by MANAGER
    @PostMapping
//    @PreAuthorize("hasRole('MANAGER')")
    public UserResponse addUser(AddUserRequest request){
        return adminService.addUserByAdmin(request) ;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ManagerResponse> getManagerById(@PathVariable Long id) {
        ManagerResponse response = managerService.getManagerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ManagerResponse>> getAllManagers() {
        List<ManagerResponse> managers = managerService.getAllManagers();
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/region/{region}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ManagerResponse>> getManagersByRegion(@PathVariable String region) {
        List<ManagerResponse> managers = managerService.getManagersByRegion(region);
        return ResponseEntity.ok(managers);
    }

    @PutMapping("/{id}/region")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ManagerResponse> updateRegion(@PathVariable Long id,
                                                        @RequestParam String region) {
        ManagerResponse response = managerService.updateRegion(id, region);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/team-size")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') and @userSecurity.isOwner(#id)")
    public ResponseEntity<ManagerResponse> updateTeamSize(@PathVariable Long id,
                                                          @RequestParam Integer teamSize) {
        ManagerResponse response = managerService.updateTeamSize(id, teamSize);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/dashboard")
    @PreAuthorize("hasRole('MANAGER') and @userSecurity.isOwner(#id)")
    public ResponseEntity<ManagerDashboardResponse> getManagerDashboard(@PathVariable Long id) {
        ManagerDashboardResponse dashboard = managerService.getManagerDashboard(id);
        return ResponseEntity.ok(dashboard);
    }
}