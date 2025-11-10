package com.logistics.tracking_service.repository;

import com.logistics.tracking_service.model.GPSPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GPSPositionRepository extends JpaRepository<GPSPosition , Long> {
    // ==================== QUERIES PAR LIVREUR ====================

    /**
     * Récupérer la dernière position d'un livreur
     * Utilisé pour afficher la position actuelle sur la carte
     *
     * @param livreurId ID du livreur
     * @return La position la plus récente
     */
    Optional<GPSPosition> findTopByLivreurIdOrderByDateHeureDesc(Long livreurId);

    /**
     * Récupérer toutes les positions d'un livreur, triées par date (plus récent en premier)
     *
     * @param livreurId ID du livreur
     * @return Liste de toutes les positions
     */
    List<GPSPosition> findByLivreurIdOrderByDateHeureDesc(Long livreurId);

    /**
     * Récupérer l'historique des positions d'un livreur sur une période donnée
     * Utile pour afficher le trajet effectué
     *
     * @param livreurId ID du livreur
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Liste des positions dans la période
     */
    List<GPSPosition> findByLivreurIdAndDateHeureBetweenOrderByDateHeureDesc(
            Long livreurId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * Compter le nombre total de positions enregistrées pour un livreur
     *
     * @param livreurId ID du livreur
     * @return Nombre de positions
     */
    Long countByLivreurId(Long livreurId);

    // ==================== QUERIES PAR LIVRAISON ====================

    /**
     * Récupérer toutes les positions d'une livraison
     * Permet de tracer le parcours d'une livraison spécifique
     *
     * @param livraisonId ID de la livraison
     * @return Liste des positions de la livraison
     */
    List<GPSPosition> findByLivraisonIdOrderByDateHeureDesc(Long livraisonId);

    /**
     * Récupérer la dernière position d'une livraison
     * Utile pour savoir où en est une livraison en cours
     *
     * @param livraisonId ID de la livraison
     * @return La position la plus récente de cette livraison
     */
    Optional<GPSPosition> findTopByLivraisonIdOrderByDateHeureDesc(Long livraisonId);

    /**
     * Compter le nombre de positions pour une livraison
     *
     * @param livraisonId ID de la livraison
     * @return Nombre de positions
     */
    Long countByLivraisonId(Long livraisonId);

    // ==================== QUERIES POUR LIVREURS ACTIFS ====================

    /**
     * Récupérer les IDs des livreurs actifs (ayant envoyé une position récemment)
     * Utile pour le dashboard admin
     *
     * @param since Date à partir de laquelle chercher (ex: maintenant - 30 minutes)
     * @return Liste des IDs des livreurs actifs
     */
    @Query("SELECT DISTINCT g.livreurId FROM GPSPosition g WHERE g.dateHeure >= :since")
    List<Long> findActiveLivreurs(@Param("since") LocalDateTime since);

    /**
     * Récupérer les livreurs en mouvement (isMoving = true)
     *
     * @param since Date minimale
     * @return Liste des IDs des livreurs en mouvement
     */
    @Query("SELECT DISTINCT g.livreurId FROM GPSPosition g " +
            "WHERE g.dateHeure >= :since AND g.isMoving = true")
    List<Long> findMovingLivreurs(@Param("since") LocalDateTime since);

    /**
     * Récupérer les livreurs arrêtés (isMoving = false)
     *
     * @param since Date minimale
     * @return Liste des IDs des livreurs arrêtés
     */
    @Query("SELECT DISTINCT g.livreurId FROM GPSPosition g " +
            "WHERE g.dateHeure >= :since AND g.isMoving = false")
    List<Long> findStoppedLivreurs(@Param("since") LocalDateTime since);

    // ==================== RECHERCHE GÉOGRAPHIQUE ====================

    /**
     * Récupérer les positions dans une zone géographique (rectangle)
     * Utilisé pour afficher les livreurs dans une zone de la carte
     *
     * @param minLat Latitude minimale
     * @param maxLat Latitude maximale
     * @param minLon Longitude minimale
     * @param maxLon Longitude maximale
     * @param since Date minimale (positions récentes uniquement)
     * @return Liste des positions dans la zone
     */
    @Query("SELECT g FROM GPSPosition g WHERE " +
            "g.latitude BETWEEN :minLat AND :maxLat AND " +
            "g.longitude BETWEEN :minLon AND :maxLon AND " +
            "g.dateHeure >= :since")
    List<GPSPosition> findPositionsInBounds(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon,
            @Param("since") LocalDateTime since
    );

    /**
     * Récupérer les dernières positions de tous les livreurs dans une zone
     *
     * @param minLat Latitude minimale
     * @param maxLat Latitude maximale
     * @param minLon Longitude minimale
     * @param maxLon Longitude maximale
     * @return Liste des positions
     */
    @Query("SELECT g FROM GPSPosition g WHERE " +
            "g.latitude BETWEEN :minLat AND :maxLat AND " +
            "g.longitude BETWEEN :minLon AND :maxLon AND " +
            "g.dateHeure = (SELECT MAX(g2.dateHeure) FROM GPSPosition g2 " +
            "WHERE g2.livreurId = g.livreurId)")
    List<GPSPosition> findLatestPositionsInBounds(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );

    // ==================== STATISTIQUES ====================

    /**
     * Calculer la vitesse moyenne de tous les livreurs
     *
     * @param since Date minimale
     * @return Vitesse moyenne en km/h
     */
    @Query("SELECT AVG(g.vitesse) FROM GPSPosition g " +
            "WHERE g.vitesse IS NOT NULL AND g.vitesse > 0 AND g.dateHeure >= :since")
    Double calculateAverageSpeed(@Param("since") LocalDateTime since);

    /**
     * Calculer la vitesse moyenne d'un livreur
     *
     * @param livreurId ID du livreur
     * @param since Date minimale
     * @return Vitesse moyenne en km/h
     */
    @Query("SELECT AVG(g.vitesse) FROM GPSPosition g " +
            "WHERE g.livreurId = :livreurId AND g.vitesse IS NOT NULL " +
            "AND g.vitesse > 0 AND g.dateHeure >= :since")
    Double calculateAverageSpeedForLivreur(
            @Param("livreurId") Long livreurId,
            @Param("since") LocalDateTime since
    );

    /**
     * Compter le nombre total de positions enregistrées
     *
     * @return Nombre total de positions
     */
    @Query("SELECT COUNT(g) FROM GPSPosition g")
    Long countTotalPositions();

    /**
     * Compter les positions sur une période
     *
     * @param startDate Date de début
     * @param endDate Date de fin
     * @return Nombre de positions
     */
    @Query("SELECT COUNT(g) FROM GPSPosition g WHERE g.dateHeure BETWEEN :startDate AND :endDate")
    Long countPositionsBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ==================== ALERTES ====================

    /**
     * Récupérer les livreurs avec vitesse excessive (> seuil)
     *
     * @param speedThreshold Seuil de vitesse en km/h
     * @param since Date minimale
     * @return Liste des positions avec vitesse excessive
     */
    @Query("SELECT g FROM GPSPosition g WHERE " +
            "g.vitesse > :speedThreshold AND g.dateHeure >= :since " +
            "ORDER BY g.vitesse DESC")
    List<GPSPosition> findExcessiveSpeedPositions(
            @Param("speedThreshold") Double speedThreshold,
            @Param("since") LocalDateTime since
    );

    /**
     * Récupérer les livreurs avec batterie faible (< seuil)
     *
     * @param batteryThreshold Seuil de batterie en %
     * @param since Date minimale
     * @return Liste des positions avec batterie faible
     */
    @Query("SELECT g FROM GPSPosition g WHERE " +
            "g.batteryLevel < :batteryThreshold AND g.dateHeure >= :since " +
            "ORDER BY g.batteryLevel ASC")
    List<GPSPosition> findLowBatteryPositions(
            @Param("batteryThreshold") Integer batteryThreshold,
            @Param("since") LocalDateTime since
    );


//     * Récupérer les livreurs arrêtés depuis longtemps
//     *
//     * @param livreurId ID du livreur
//     * @param minutess Nombre de minutes d'arrêt
//     * @return True si arrêté depuis plus de X minutes

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GPSPosition g " +
            "WHERE g.livreurId = :livreurId AND g.isMoving = false " +
            "AND g.dateHeure >= :since")
    Boolean isLivreurStoppedForLong(
            @Param("livreurId") Long livreurId,
            @Param("since") LocalDateTime since
    );

    // ==================== NETTOYAGE ====================

    /**
     * Supprimer les positions plus anciennes qu'une date donnée
     * Utilisé pour le nettoyage automatique (scheduler)
     *
     * @param date Date limite
     */
    @Modifying
    @Query("DELETE FROM GPSPosition g WHERE g.dateHeure < :date")
    void deleteByDateHeureBefore(@Param("date") LocalDateTime date);

    /**
     * Supprimer toutes les positions d'un livreur
     *
     * @param livreurId ID du livreur
     */
    @Modifying
    @Query("DELETE FROM GPSPosition g WHERE g.livreurId = :livreurId")
    void deleteByLivreurId(@Param("livreurId") Long livreurId);

    /**
     * Supprimer toutes les positions d'une livraison
     *
     * @param livraisonId ID de la livraison
     */
    @Modifying
    @Query("DELETE FROM GPSPosition g WHERE g.livraisonId = :livraisonId")
    void deleteByLivraisonId(@Param("livraisonId") Long livraisonId);

    // ==================== QUERIES AVANCÉES ====================

    /**
     * Récupérer les dernières positions de tous les livreurs actifs
     * Optimisé pour afficher tous les livreurs sur la carte
     *
     * @param since Date minimale
     * @return Liste des dernières positions
     */
    @Query("SELECT g FROM GPSPosition g WHERE g.dateHeure = " +
            "(SELECT MAX(g2.dateHeure) FROM GPSPosition g2 " +
            "WHERE g2.livreurId = g.livreurId AND g2.dateHeure >= :since)")
    List<GPSPosition> findLatestPositionsForAllLivreurs(@Param("since") LocalDateTime since);

    /**
     * Récupérer l'historique complet d'une livraison avec pagination
     *
     * @param livraisonId ID de la livraison
     * @param limit Nombre maximum de résultats
     * @return Liste des positions (limitée)
     */
    @Query(value = "SELECT * FROM gps_positions WHERE livraison_id = :livraisonId " +
            "ORDER BY date_heure DESC LIMIT :limit", nativeQuery = true)
    List<GPSPosition> findTopNByLivraisonId(
            @Param("livraisonId") Long livraisonId,
            @Param("limit") int limit
    );

    /**
     * Vérifier si un livreur a des positions récentes
     *
     * @param livreurId ID du livreur
     * @param since Date minimale
     * @return True si le livreur a envoyé des positions récemment
     */
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM GPSPosition g " +
            "WHERE g.livreurId = :livreurId AND g.dateHeure >= :since")
    Boolean hasRecentPositions(
            @Param("livreurId") Long livreurId,
            @Param("since") LocalDateTime since
    );

    /**
     * Récupérer les positions avec une précision GPS inférieure à un seuil
     * (précision faible = position peu fiable)
     *
     * @param maxPrecision Précision maximale acceptée en mètres
     * @param since Date minimale
     * @return Liste des positions avec précision insuffisante
     */
    @Query("SELECT g FROM GPSPosition g WHERE " +
            "g.precision > :maxPrecision AND g.dateHeure >= :since")
    List<GPSPosition> findLowPrecisionPositions(
            @Param("maxPrecision") Double maxPrecision,
            @Param("since") LocalDateTime since
    );

}
