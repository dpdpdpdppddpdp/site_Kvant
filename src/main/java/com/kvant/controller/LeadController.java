package com.kvant.controller;

import com.kvant.dto.LeadRequestDto;
import com.kvant.entity.Lead;
import com.kvant.entity.User;
import com.kvant.service.ExcelService;
import com.kvant.service.LeadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*")
public class LeadController {

    private static final Logger logger = LoggerFactory.getLogger(LeadController.class);

    private final LeadService leadService;
    private final ExcelService excelService;

    public LeadController(LeadService leadService, ExcelService excelService) {
        this.leadService = leadService;
        this.excelService = excelService;
    }

    @PostMapping
    public ResponseEntity<?> submitLead(@Valid @RequestBody LeadRequestDto dto,
                                        Authentication authentication) {
        try {
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                userId = ((User) authentication.getPrincipal()).getId();
            }
            logger.info("Received lead from: {}, phone: {}, userId: {}", dto.getFirstName(), dto.getPhone(), userId);
            Lead lead = leadService.createLead(dto, userId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Заявка принята. Менеджер свяжется с вами.",
                "leadId", lead.getId()
            ));
        } catch (Exception e) {
            logger.error("Error submitting lead: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "Ошибка при сохранении заявки. Пожалуйста, попробуйте позже.",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllLeads() {
        try {
            return ResponseEntity.ok(leadService.getAllLeads());
        } catch (Exception e) {
            logger.error("Error fetching leads: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "Ошибка загрузки заявок."));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyLeads(Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
                return ResponseEntity.status(401).body(Map.of("error", "Не авторизован"));
            }
            Long userId = ((User) authentication.getPrincipal()).getId();
            return ResponseEntity.ok(leadService.getLeadsByUser(userId));
        } catch (Exception e) {
            logger.error("Error fetching user leads: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLead(@PathVariable Long id,
                                        @RequestBody Map<String, String> fields) {
        try {
            Lead updated = leadService.updateLead(id, fields);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating lead {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<?> exportAllLeadsToExcel() {
        try {
            List<Lead> leads = leadService.getAllLeads();
            byte[] excelData = excelService.generateAllLeadsExcel(leads);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "leads_" + System.currentTimeMillis() + ".xlsx");
            return ResponseEntity.ok().headers(headers).body(excelData);
        } catch (Exception e) {
            logger.error("Error exporting leads: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}