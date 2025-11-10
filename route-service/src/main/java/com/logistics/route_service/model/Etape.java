package com.logistics.route_service.model;

import com.logistics.route_service.enums.TypeRoute;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "etapes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEtape;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itineraire_id", nullable = false)
    private Itineraire itineraire;

    @Column(nullable = false)
    private Integer ordre; // Ordre de l'étape

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "nom_lieu")
    private String nomLieu;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_route")
    private TypeRoute typeRoute;

    @Column(name = "distance_depuis_precedent")
    private Double distanceDepuisPrecedent; // En km

    @Column(name = "duree_depuis_precedent")
    private Integer dureeDepuisPrecedent; // En minutes

    @Column(name = "instructions")
    private String instructions; // Ex: "Tournez à droite sur Avenue Hassan II"
}