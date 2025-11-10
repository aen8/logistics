package com.logistics.user_service.repository;

import com.logistics.user_service.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin , Long> {
}
