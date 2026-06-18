package com.kvant.repository;

import com.kvant.entity.SiteContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteContentRepository extends JpaRepository<SiteContent, Long> {
    Optional<SiteContent> findByKey(String key);
    List<SiteContent> findBySection(String section);
    List<SiteContent> findByContentType(String contentType);
    boolean existsByKey(String key);
}
