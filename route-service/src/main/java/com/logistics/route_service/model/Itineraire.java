package com.logistics.route_service.model;

import com.logistics.route_service.enums.StatutItineraire;
import com.logistics.route_service.enums.TypeCalcul;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itineraires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Itineraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItineraire;

    @Column(name = "livraison_id")
    private Long livraisonId; // ID de la livraison associée

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCalcul typeCalcul;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutItineraire statut;

    // Point de départ
    @Column(name = "latitude_depart", nullable = false)
    private Double latitudeDepart;

    @Column(name = "longitude_depart", nullable = false)
    private Double longitudeDepart;

    @Column(name = "adresse_depart")
    private String adresseDepart;

    // Point d'arrivée
    @Column(name = "latitude_arrivee", nullable = false)
    private Double latitudeArrivee;

    @Column(name = "longitude_arrivee", nullable = false)
    private Double longitudeArrivee;

    @Column(name = "adresse_arrivee")
    private String adresseArrivee;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(name = "duree_minutes", nullable = false)
    private Integer dureeMinutes;

    @Column(name = "cout_estime")
    private Double coutEstime;

    @Column(name = "cout_carburant")
    private Double coutCarburant;

    @Column(name = "emissions_co2")
    private Double emissionsCo2; // En kg

    @Column(name = "vitesse_moyenne")
    private Double vitesseMoyenne;

    @Column(name = "polyline", length = 5000)
    private String polyline; // Encodage de l'itinéraire (Google Polyline ou GeoJSON)

    @OneToMany(mappedBy = "itineraire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Etape> etapes = new ArrayList<>();

    @Column(name = "date_calcul", nullable = false)
    private LocalDateTime dateCalcul;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dateCalcul == null) {
            dateCalcul = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutItineraire.CALCULE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addEtape(Etape etape) {
        etapes.add(etape);
        etape.setItineraire(this);
    }
}