//package com.logistics.user_service.service;
//
//
//import com.logistics.user_service.dtos.LoginRequest;
//import com.logistics.user_service.dtos.RegisterRequest;
//import com.logistics.user_service.dtos.response.AuthResponse;
//import com.logistics.user_service.exceptions.EmailAlreadyExistsException;
//import com.logistics.user_service.exceptions.InvalidCredentialsException;
//import com.logistics.user_service.model.*;
//import com.logistics.user_service.repository.*;
//import com.logistics.user_service.security.JwtTokenProvider;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final ClientRepository clientRepository;
//    private final LivreurRepository livreurRepository;
//    private final AdminRepository adminRepository;
//    private final ManagerRepository managerRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManager authenticationManager;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Transactional
//    public AuthResponse register(RegisterRequest request) {
//        log.info("Registering new user with email: {}", request.getEmail());
//
//        // Check if email already exists
//        if (userRepository.existsByEmail(request.getEmail())) {
//
//            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
//        }
//
//        // Create user based on role
//        User user = createUserByRole(request);
//
//        // Save user
//        User savedUser = saveUserByRole(user, request.getRole());
//
//        // Generate tokens
//        String token = jwtTokenProvider.generateToken(savedUser.getEmail());
//        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getEmail());
//
//        log.info("User registered successfully with ID: {}", savedUser.getIdUser());
//
//        return AuthResponse.builder()
//                .token(token)
//                .refreshToken(refreshToken)
//                .userId(savedUser.getIdUser())
//                .email(savedUser.getEmail())
//                .role(savedUser.getRole())
//                .nom(getNameFromUser(savedUser))
//                .build();
//    }
//
//    @Transactional(readOnly = true)
//    public AuthResponse login(LoginRequest request) {
//        log.info("User attempting to login: {}", request.getEmail());
//
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            User user = userRepository.findByEmail(request.getEmail())
//                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
//
//            String token = jwtTokenProvider.generateToken(authentication);
//            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());
//
//            log.info("User logged in successfully: {}", request.getEmail());
//
//            return AuthResponse.builder()
//                    .token(token)
//                    .refreshToken(refreshToken)
//                    .userId(user.getIdUser())
//                    .email(user.getEmail())
//                    .role(user.getRole())
//                    .nom(getNameFromUser(user))
//                    .build();
//
//        } catch (Exception e) {
//            log.error("Login failed for user: {}", request.getEmail());
//            throw new InvalidCredentialsException("Invalid email or password");
//        }
//    }
//
//    private User createUserByRole(RegisterRequest request) {
//        String hashedPassword = passwordEncoder.encode(request.getPassword());
//
//        return switch (request.getRole()) {
//            case "CLIENT" -> {
//                Client client = new Client();
//                client.setEmail(request.getEmail());
//                client.setPasswordHash(hashedPassword);
//                client.setRole("CLIENT");
//                client.setNom(request.getNom());
//                client.setTelephone(request.getTelephone());
//
//                // Add address if provided
//                if (request.getAdresse() != null) {
//                    Adresse adresse = new Adresse();
//                    adresse.setRue(request.getAdresse().getRue());
//                    adresse.setVille(request.getAdresse().getVille());
//                    adresse.setCodePostal(request.getAdresse().getCodePostal());
//                    adresse.setPays(request.getAdresse().getPays());
//                    adresse.setLatitude(request.getAdresse().getLatitude());
//                    adresse.setLongitude(request.getAdresse().getLongitude());
//                    adresse.setEstPrincipale(true);
//                    client.addAdresse(adresse);
//                }
//                yield client;
//            }
//            case "LIVREUR" -> {
//                Livreur livreur = new Livreur();
//                livreur.setEmail(request.getEmail());
//                livreur.setPasswordHash(hashedPassword);
//                livreur.setRole("LIVREUR");
//                livreur.setNom(request.getNom());
//                livreur.setTelephone(request.getTelephone());
//                livreur.setDisponibilite(true);
//                yield livreur;
//            }
//            case "ADMIN" -> {
//                Admin admin = new Admin();
//                admin.setEmail(request.getEmail());
//                admin.setPasswordHash(hashedPassword);
//                admin.setRole("ADMIN");
//                admin.setNom(request.getNom());
//                admin.setTelephone(request.getTelephone());
//                yield admin;
//            }
//            case "MANAGER" -> {
//                Manager manager = new Manager();
//                manager.setEmail(request.getEmail());
//                manager.setPasswordHash(hashedPassword);
//                manager.setRole("MANAGER");
//                manager.setNom(request.getNom());
//                manager.setTelephone(request.getTelephone());
//                yield manager;
//            }
//            default -> throw new IllegalArgumentException("Invalid role: " + request.getRole());
//        };
//    }
//
//    private User saveUserByRole(User user, String role) {
//        return switch (role) {
//            case "CLIENT" -> clientRepository.save((Client) user);
//            case "LIVREUR" -> livreurRepository.save((Livreur) user);
//            case "ADMIN" -> adminRepository.save((Admin) user);
//            case "MANAGER" -> managerRepository.save((Manager) user);
//            default -> throw new IllegalArgumentException("Invalid role: " + role);
//        };
//    }
//
//    private String getNameFromUser(User user) {
//        if (user instanceof Client) {
//            return ((Client) user).getNom();
//        } else if (user instanceof Livreur) {
//            return ((Livreur) user).getNom();
//        } else if (user instanceof Admin) {
//            return ((Admin) user).getNom();
//        } else if (user instanceof Manager) {
//            return ((Manager) user).getNom();
//        }
//        return "Unknown";
//    }
//}


package com.logistics.user_service.service;

import com.logistics.user_service.dtos.LoginRequest;
import com.logistics.user_service.dtos.RegisterRequest;
import com.logistics.user_service.dtos.response.AuthResponse;
import com.logistics.user_service.enums.UserRole;
import com.logistics.user_service.exceptions.EmailAlreadyExistsException;
import com.logistics.user_service.exceptions.InvalidCredentialsException;
import com.logistics.user_service.model.*;
import com.logistics.user_service.repository.*;
import com.logistics.user_service.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final LivreurRepository livreurRepository;
    private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        // Determine role automatically based on email
        UserRole assignedRole = determineRoleFromEmail(request.getEmail());

        // Create user
        User user = createUser(request, assignedRole);

        // Save user
        User savedUser = saveUserByRole(user, assignedRole);

        // Generate tokens
        String token = jwtTokenProvider.generateToken(savedUser.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getEmail());

        log.info("User registered successfully with ID: {}", savedUser.getIdUser());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(savedUser.getIdUser())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .nom(getNameFromUser(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("User attempting to login: {}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

            String token = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

            log.info("User logged in successfully: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .userId(user.getIdUser())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .nom(getNameFromUser(user))
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    // ------------------- HELPER METHODS -------------------

    private UserRole determineRoleFromEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> UserRole.valueOf(user.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }


    @Transactional(readOnly = true)
    public User createUser(RegisterRequest request, UserRole role) {

            String hashedPassword = passwordEncoder.encode(request.getPassword());


        return switch (role) {
            case CLIENT -> {
                Client client = new Client();
                client.setEmail(request.getEmail());
                client.setPasswordHash(hashedPassword);
                client.setRole(String.valueOf(UserRole.CLIENT));
                client.setNom(request.getNom());
                client.setTelephone(request.getTelephone());

//                if (request.getAdresse() != null) {
//                    Adresse adresse = new Adresse();
//                    adresse.setRue(request.getAdresse().getRue());
//                    adresse.setVille(request.getAdresse().getVille());
//                    adresse.setCodePostal(request.getAdresse().getCodePostal());
//                    adresse.setPays(request.getAdresse().getPays());
//                    adresse.setLatitude(request.getAdresse().getLatitude());
//                    adresse.setLongitude(request.getAdresse().getLongitude());
//                    adresse.setEstPrincipale(true);
//                    client.addAdresse(adresse);
//                }
                yield client;
            }
            case LIVREUR -> {
                Livreur livreur = new Livreur();
                livreur.setEmail(request.getEmail());
                livreur.setPasswordHash(hashedPassword);
                livreur.setRole(String.valueOf(UserRole.LIVREUR));
                livreur.setNom(request.getNom());
                livreur.setTelephone(request.getTelephone());
                livreur.setDisponibilite(true);
                yield livreur;
            }
            case ADMIN -> {
                Admin admin = new Admin();
                admin.setEmail(request.getEmail());
                admin.setPasswordHash(hashedPassword);
                admin.setRole(String.valueOf(UserRole.ADMIN));
                admin.setNom(request.getNom());
                admin.setTelephone(request.getTelephone());
                yield admin;
            }
            case MANAGER -> {
                Manager manager = new Manager();
                manager.setEmail(request.getEmail());
                manager.setPasswordHash(hashedPassword);
                manager.setRole(String.valueOf(UserRole.MANAGER));
                manager.setNom(request.getNom());
                manager.setTelephone(request.getTelephone());
                yield manager;
            }
        };
    }

    public User saveUserByRole(User user, UserRole role) {
        return switch (role) {
            case CLIENT -> clientRepository.save((Client) user);
            case LIVREUR -> livreurRepository.save((Livreur) user);
            case ADMIN -> adminRepository.save((Admin) user);
            case MANAGER -> managerRepository.save((Manager) user);
        };
    }

    private String getNameFromUser(User user) {
        if (user instanceof Client) return ((Client) user).getNom();
        if (user instanceof Livreur) return ((Livreur) user).getNom();
        if (user instanceof Admin) return ((Admin) user).getNom();
        if (user instanceof Manager) return ((Manager) user).getNom();
        return "Unknown";
    }
}
