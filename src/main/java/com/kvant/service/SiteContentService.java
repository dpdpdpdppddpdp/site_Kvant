package com.kvant.service;

import com.kvant.entity.SiteContent;
import com.kvant.repository.SiteContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SiteContentService {

    private static final Logger logger = LoggerFactory.getLogger(SiteContentService.class);

    private final SiteContentRepository siteContentRepository;

    public SiteContentService(SiteContentRepository siteContentRepository) {
        this.siteContentRepository = siteContentRepository;
    }

    public List<SiteContent> getAllContent() {
        return siteContentRepository.findAll();
    }

    public List<SiteContent> getContentBySection(String section) {
        return siteContentRepository.findBySection(section);
    }

    public Optional<SiteContent> getContentByKey(String key) {
        return siteContentRepository.findByKey(key);
    }

    public Map<String, String> getContentAsMap() {
        return siteContentRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(
                        SiteContent::getKey,
                        SiteContent::getValue
                ));
    }

    @Transactional
    public SiteContent createContent(SiteContent content) {
        logger.info("Creating content with key: {}", content.getKey());
        if (siteContentRepository.existsByKey(content.getKey())) {
            throw new IllegalArgumentException("Content with key " + content.getKey() + " already exists");
        }
        return siteContentRepository.save(content);
    }

    @Transactional
    public SiteContent updateContent(String key, String value, Long userId) {
        logger.info("Updating content with key: {}", key);
        SiteContent content = siteContentRepository.findByKey(key)
                .orElseGet(() -> {
                    logger.info("Content with key {} not found, creating new entry", key);
                    String section = key.contains("_") ? key.substring(0, key.indexOf('_')) : "other";
                    SiteContent created = new SiteContent(key, value, section, "text");
                    created.setCreatedBy(userId);
                    return created;
                });
        content.setValue(value);
        content.setUpdatedBy(userId);
        content.setUpdatedAt(LocalDateTime.now());
        return siteContentRepository.save(content);
    }

    @Transactional
    public SiteContent updateContent(Long id, SiteContent content, Long userId) {
        logger.info("Updating content with id: {}", id);
        SiteContent existingContent = siteContentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Content with id " + id + " not found"));
        existingContent.setValue(content.getValue());
        existingContent.setSection(content.getSection());
        existingContent.setContentType(content.getContentType());
        existingContent.setUpdatedBy(userId);
        existingContent.setUpdatedAt(LocalDateTime.now());
        return siteContentRepository.save(existingContent);
    }

    @Transactional
    public void deleteContent(Long id) {
        logger.info("Deleting content with id: {}", id);
        siteContentRepository.deleteById(id);
    }

    @Transactional
    public void initializeDefaultContent() {
        logger.info("Initializing default content");
        
        if (!siteContentRepository.existsByKey("hero_title")) {
            siteContentRepository.save(new SiteContent("hero_title", "Строительство домов под ключ", "hero", "text"));
        }
        if (!siteContentRepository.existsByKey("hero_subtitle")) {
            siteContentRepository.save(new SiteContent("hero_subtitle", "Комплексные решения для дома и сада", "hero", "text"));
        }
        if (!siteContentRepository.existsByKey("hero_cta")) {
            siteContentRepository.save(new SiteContent("hero_cta", "Получить консультацию", "hero", "text"));
        }
        if (!siteContentRepository.existsByKey("stats_projects")) {
            siteContentRepository.save(new SiteContent("stats_projects", "500+", "stats", "number"));
        }
        if (!siteContentRepository.existsByKey("stats_experience")) {
            siteContentRepository.save(new SiteContent("stats_experience", "15", "stats", "number"));
        }
        if (!siteContentRepository.existsByKey("stats_satisfaction")) {
            siteContentRepository.save(new SiteContent("stats_satisfaction", "98%", "stats", "text"));
        }
        if (!siteContentRepository.existsByKey("stats_clients")) {
            siteContentRepository.save(new SiteContent("stats_clients", "100+", "stats", "number"));
        }
        if (!siteContentRepository.existsByKey("contact_phone")) {
            siteContentRepository.save(new SiteContent("contact_phone", "+7 (929) 425-97-74", "contact", "text"));
        }
        if (!siteContentRepository.existsByKey("contact_email")) {
            siteContentRepository.save(new SiteContent("contact_email", "info@kvant.ru", "contact", "text"));
        }
        if (!siteContentRepository.existsByKey("contact_address_vladivostok")) {
            siteContentRepository.save(new SiteContent("contact_address_vladivostok", "Владивосток", "contact", "text"));
        }
        if (!siteContentRepository.existsByKey("contact_address_ussuriysk")) {
            siteContentRepository.save(new SiteContent("contact_address_ussuriysk", "Уссурийск", "contact", "text"));
        }
        
        logger.info("Default content initialized");
    }
}
