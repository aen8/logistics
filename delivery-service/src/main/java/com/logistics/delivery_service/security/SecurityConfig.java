package com.logistics.delivery_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/packages/tracking/**").permitAll() // Tracking public

                        // Endpoints pour les clients
                        .requestMatchers("/api/deliveries/client/**").authenticated()

                        // Endpoints pour les livreurs
                        .requestMatchers("/api/deliveries/driver/**").authenticated()
                        .requestMatchers("/api/packages/*/scan").authenticated()

                        // Endpoints admin/manager
                        .requestMatchers("/api/deliveries").permitAll()
                        .requestMatchers("/api/deliveries/*/status").permitAll()
                        .requestMatchers("/api/deliveries/assign").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Tout le reste nécessite authentification
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}