package com.logistics.delivery_service.model;

import com.logistics.delivery_service.enums.EtatLivraison;
import com.logistics.delivery_service.enums.TypeLivraison;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livraisons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLivraison;

    @Column(name = "order_id", nullable = false)
    private Long orderId; // ID de la commande dans order-service

    @Column(name = "client_id", nullable = false)
    private Long clientId; // ID du client dans user-service

    @Column(name = "livreur_id")
    private Long livreurId; // ID du livreur dans user-service

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_livraison_prevue")
    private LocalDateTime dateLivraisonPrevue;

    @Column(name = "date_livraison_effective")
    private LocalDateTime dateLivraisonEffective;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatLivraison etat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLivraison type;

    @Column(name = "distance_km")
    private Double distanceKm;

    // Adresse de départ (entrepôt)
    @Column(name = "adresse_origine_rue")
    private String adresseOrigineRue;

    @Column(name = "adresse_origine_ville")
    private String adresseOrigineVille;

    @Column(name = "adresse_origine_code_postal")
    private String adresseOrigineCodePostal;

    @Column(name = "latitude_origine")
    private Double latitudeOrigine;

    @Column(name = "longitude_origine")
    private Double longitudeOrigine;

    // Adresse de destination (client)
    @Column(name = "adresse_destination_rue", nullable = false)
    private String adresseDestinationRue;

    @Column(name = "adresse_destination_ville", nullable = false)
    private String adresseDestinationVille;

    @Column(name = "adresse_destination_code_postal", nullable = false)
    private String adresseDestinationCodePostal;

    @Column(name = "latitude_destination")
    private Double latitudeDestination;

    @Column(name = "longitude_destination")
    private Double longitudeDestination;

    @Column(name = "notes")
    private String notes;

    @Column(name = "prix_livraison")
    private Double prixLivraison;

    @OneToMany(mappedBy = "livraison", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Colis> colisList = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
        if (etat == null) {
            etat = EtatLivraison.EN_ATTENTE;
        }
        if (type == null) {
            type = TypeLivraison.STANDARD;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addColis(Colis colis) {
        colisList.add(colis);
        colis.setLivraison(this);
    }

    public void removeColis(Colis colis) {
        colisList.remove(colis);
        colis.setLivraison(null);
    }
}