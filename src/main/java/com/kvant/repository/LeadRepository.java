package com.kvant.repository;

import com.kvant.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByStatus(String status);
    List<Lead> findByPhone(String phone);
    List<Lead> findAllByOrderByCreatedAtDesc();
    List<Lead> findByUserIdOrderByCreatedAtDesc(Long userId);
}