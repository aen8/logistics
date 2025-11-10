package com.logistics.notification_service.repository;

import com.logistics.notification_service.enums.StatutNotification;
import com.logistics.notification_service.enums.TypeNotification;
import com.logistics.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndStatut(Long userId, StatutNotification statut);

    List<Notification> findByStatut(StatutNotification statut);

    List<Notification> findByType(TypeNotification type);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.statut = :statut AND n.nombreTentatives < 3")
    List<Notification> findPendingNotifications(@Param("statut") StatutNotification statut);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.dateLecture IS NULL")
    List<Notification> findUnreadNotifications(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.dateLecture IS NULL")
    Long countUnreadNotifications(@Param("userId") Long userId);

    Long countByStatut(StatutNotification statut);

    Long countByUserIdAndStatut(Long userId, StatutNotification statut);

    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate")
    List<Notification> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
}