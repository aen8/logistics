package com.logistics.user_service.repository;

import com.logistics.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User ,Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
   //COUNT ACTIVE USERS
    Long countByActiveTrue();
    @Query("SELECT EXTRACT(MONTH FROM u.createdAt) AS month, COUNT(u) AS count FROM User u GROUP BY EXTRACT(MONTH FROM u.createdAt) ORDER BY month")
    List<Object[]> countUsersByMonth();
}
