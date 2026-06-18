package com.kvant.controller;

import com.kvant.entity.SiteContent;
import com.kvant.service.SiteContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content")
@CrossOrigin(origins = "*")
public class SiteContentController {

    private static final Logger logger = LoggerFactory.getLogger(SiteContentController.class);

    private final SiteContentService siteContentService;

    public SiteContentController(SiteContentService siteContentService) {
        this.siteContentService = siteContentService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getAllContent() {
        return ResponseEntity.ok(siteContentService.getContentAsMap());
    }

    @GetMapping("/section/{section}")
    public ResponseEntity<List<SiteContent>> getContentBySection(@PathVariable String section) {
        return ResponseEntity.ok(siteContentService.getContentBySection(section));
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<SiteContent> getContentByKey(@PathVariable String key) {
        return siteContentService.getContentByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createContent(@RequestBody SiteContent content, Authentication authentication) {
        try {
            SiteContent created = siteContentService.createContent(content);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating content: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/key/{key}")
    public ResponseEntity<?> updateContentByKey(@PathVariable String key, @RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String value = request.get("value");
            Long userId = getUserIdFromAuthentication(authentication);
            SiteContent updated = siteContentService.updateContent(key, value, userId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating content: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateContent(@PathVariable Long id, @RequestBody SiteContent content, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            SiteContent updated = siteContentService.updateContent(id, content, userId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating content: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContent(@PathVariable Long id) {
        try {
            siteContentService.deleteContent(id);
            return ResponseEntity.ok(Map.of("message", "Content deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting content: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/initialize")
    public ResponseEntity<?> initializeDefaultContent() {
        try {
            siteContentService.initializeDefaultContent();
            return ResponseEntity.ok(Map.of("message", "Default content initialized successfully"));
        } catch (Exception e) {
            logger.error("Error initializing default content: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof com.kvant.entity.User) {
            return ((com.kvant.entity.User) authentication.getPrincipal()).getId();
        }
        return null;
    }
}
