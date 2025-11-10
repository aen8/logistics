package com.logistics.user_service;

import com.logistics.user_service.model.*;
import com.logistics.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
public class UserServiceApplication {

    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(UserRepository userRepository) {
//        return args -> {
//            // Create a CLIENT
//            Client client = new Client();
//            for (int i = 0; i <3 ; i++) {
//                client.setEmail("client"+i+"@example.com");
//                client.setPasswordHash(passwordEncoder.encode("client123"));
//                client.setRole("CLIENT");
//                client.setNom("John Doe"+i);
//                client.setTelephone("+212600123456");
//            }
//
//            Adresse adresse = new Adresse();
//            adresse.setRue("123 Main Street");
//            adresse.setVille("Casablanca");
//            adresse.setCodePostal("20000");
//            adresse.setPays("Morocco");
//            adresse.setLatitude(33.5731);
//            adresse.setLongitude(-7.5898);
//            adresse.setEstPrincipale(true);
//            client.addAdresse(adresse);
//
//            userRepository.save(client);
//
//            // Create a LIVREUR
//            Livreur livreur = new Livreur();
//            livreur.setEmail("livreur@example.com");
//            livreur.setPasswordHash(passwordEncoder.encode("livreur123"));
//            livreur.setRole("LIVREUR");
//            livreur.setNom("Ahmed Ben");
//            livreur.setTelephone("+212600654321");
//            livreur.setDisponibilite(true);
//
//            userRepository.save(livreur);
//
//            // Create an ADMIN
//            Admin admin = new Admin();
//            admin.setEmail("admin@example.com");
//            admin.setPasswordHash(passwordEncoder.encode("admin123"));
//            admin.setRole("ADMIN");
//            admin.setNom("Admin User");
//            admin.setTelephone("+212600111222");
//
//            userRepository.save(admin);
//
//            // Create a MANAGER
//            Manager manager = new Manager();
//            manager.setEmail("manager@example.com");
//            manager.setPasswordHash(passwordEncoder.encode("manager123"));
//            manager.setRole("MANAGER");
//            manager.setNom("Manager User");
//            manager.setTelephone("+212600333444");
//
//            userRepository.save(manager);
//
//            System.out.println("Initial users created successfully!");
//        };
//    }
}
