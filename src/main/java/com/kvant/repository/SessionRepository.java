package com.kvant.repository;

import com.kvant.entity.Session;
import com.kvant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    Optional<Session> findByToken(String token);
    
    List<Session> findByUser(User user);
    
    List<Session> findByUserAndIsActiveTrue(User user);
    
    void deleteByExpiresAtBefore(LocalDateTime date);
    
    void deleteByUser(User user);
}
