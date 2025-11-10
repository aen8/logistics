package com.logistics.user_service.service;

import com.logistics.user_service.dtos.AddUserRequest;
import com.logistics.user_service.dtos.AdresseRequest;
import com.logistics.user_service.dtos.RegisterRequest;
import com.logistics.user_service.dtos.response.DashboardResponse;
import com.logistics.user_service.dtos.response.UserResponse;
import com.logistics.user_service.enums.UserRole;
import com.logistics.user_service.exceptions.EmailAlreadyExistsException;
import com.logistics.user_service.exceptions.UserNotFoundException;

import com.logistics.user_service.model.*;
import com.logistics.user_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final LivreurRepository livreurRepository;
    private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final AuthService authService ;

    @Transactional
    public UserResponse addUserByAdmin(AddUserRequest addUserRequest) {
        log.info("Admin is adding a new user with email: {}", addUserRequest.getEmail());

        if (userRepository.existsByEmail(addUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + addUserRequest.getEmail());
        }

        // Crée un RegisterRequest temporaire
        RegisterRequest request = new RegisterRequest();
        request.setEmail(addUserRequest.getEmail());
        request.setPassword(addUserRequest.getPassword());
        request.setNom(addUserRequest.getNom());
        request.setTelephone(addUserRequest.getTelephone());
//        request.setAdresse(addUserRequest.getAdresse());
        // On n'utilise plus la valeur de role dans RegisterRequest, mais on la transmet à la méthode
        // pour choisir quel type de User créer
        User user = authService.createUser(request, addUserRequest.getRole());

        // Sauvegarde selon le rôle
        User savedUser = authService.saveUserByRole(user, addUserRequest.getRole());

        log.info("User added successfully by admin: {}", savedUser.getEmail());
        return mapToUserResponse(savedUser);
    }


    //=============> RETURN DASHBOARD STATISTICS
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardStatistics() {
        log.info("Fetching dashboard statistics");

        long totalUsers = userRepository.count();
        long totalClients = clientRepository.count();
        long totalLivreurs = livreurRepository.count();
        long totalManagers = managerRepository.count();
        long totalAdmins = adminRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long availableLivreurs = livreurRepository.countByDisponibiliteAndActiveTrue(true);

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalClients(totalClients)
                .totalLivreurs(totalLivreurs)
                .totalManagers(totalManagers)
                .totalAdmins(totalAdmins)
                .activeUsers(activeUsers)
                .availableLivreurs(availableLivreurs)
                .monthlyUsers(getMonthlyUserCounts())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse activateUser(Long userId) {
        log.info("Activating user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setActive(true);
        User updatedUser = userRepository.save(user);

        log.info("User activated successfully with ID: {}", userId);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse deactivateUser(Long userId) {
        log.info("Deactivating user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setActive(false);
        User updatedUser = userRepository.save(user);

        log.info("User deactivated successfully with ID: {}", userId);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        userRepository.delete(user);


        log.info("User deleted successfully with ID: {}", userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getUsersByRole() {
        log.info("Fetching users count by role");

        Map<String, Long> stats = new HashMap<>();
        stats.put("CLIENT", clientRepository.count());
        stats.put("LIVREUR", livreurRepository.count());
        stats.put("ADMIN", adminRepository.count());
        stats.put("MANAGER", managerRepository.count());

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getActiveUsersReport() {
        log.info("Generating active users report");

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();
        long inactiveUsers = totalUsers - activeUsers;

        Map<String, Object> report = new HashMap<>();
        report.put("totalUsers", totalUsers);
        report.put("activeUsers", activeUsers);
        report.put("inactiveUsers", inactiveUsers);
        report.put("activePercentage", totalUsers > 0 ? (activeUsers * 100.0 / totalUsers) : 0);

        return report;
    }
    public Map<String, Long> getMonthlyUserCounts() {
        List<Object[]> results = userRepository.countUsersByMonth();

        Map<Integer, Long> monthToCount = new HashMap<>();
        for (Object[] row : results) {
            int monthNumber = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            monthToCount.put(monthNumber, count);
        }

        // ✅ Always include all 12 months
        Map<String, Long> monthlyCounts = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            String monthName = Month.of(i).name(); // JANUARY, FEBRUARY, ...
            monthlyCounts.put(monthName, monthToCount.getOrDefault(i, 0L));
        }

        return monthlyCounts;
    }


    private UserResponse mapToUserResponse(User user) {
        String nom = "Unknown";
        String telephone = "N/A";

        if (user instanceof Client) {
            nom = ((Client) user).getNom();
            telephone = ((Client) user).getTelephone();
        } else if (user instanceof Livreur) {
            nom = ((Livreur) user).getNom();
            telephone = ((Livreur) user).getTelephone();
        } else if (user instanceof Admin) {
            nom = ((Admin) user).getNom();
            telephone = ((Admin) user).getTelephone();
        } else if (user instanceof Manager) {
            nom = ((Manager) user).getNom();
            telephone = ((Manager) user).getTelephone();
        }

        return UserResponse.builder()
                .idUser(user.getIdUser())
                .email(user.getEmail())
                .role(user.getRole())
                .nom(nom)
                .telephone(telephone)
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}