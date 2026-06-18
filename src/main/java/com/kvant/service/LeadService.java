package com.kvant.service;

import com.kvant.dto.LeadRequestDto;
import com.kvant.entity.Client;
import com.kvant.entity.Lead;
import com.kvant.repository.ClientRepository;
import com.kvant.repository.LeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LeadService {

    private static final Logger logger = LoggerFactory.getLogger(LeadService.class);

    private final LeadRepository leadRepository;
    private final ClientRepository clientRepository;
    private final EmailService emailService;

    public LeadService(LeadRepository leadRepository, ClientRepository clientRepository, EmailService emailService) {
        this.leadRepository = leadRepository;
        this.clientRepository = clientRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Lead createLead(LeadRequestDto dto, Long userId) {
        try {
            logger.info("Creating new lead from: {}, phone: {}", dto.getFirstName(), dto.getPhone());

            Optional<Client> existingClient = clientRepository.findByPhoneOrEmail(dto.getPhone(), dto.getEmail());

            Lead lead = new Lead();
            lead.setFirstName(dto.getFirstName());
            lead.setLastName(dto.getLastName());
            lead.setPhone(dto.getPhone());
            lead.setEmail(dto.getEmail());
            lead.setServiceType(dto.getServiceType());
            lead.setComment(dto.getComment());
            lead.setUtmSource(dto.getUtmSource());
            lead.setUtmCampaign(dto.getUtmCampaign());
            lead.setUtmMedium(dto.getUtmMedium());
            lead.setUtmContent(dto.getUtmContent());
            lead.setReferrerUrl(dto.getReferrerUrl());
            lead.setCreatedAt(LocalDateTime.now());
            lead.setStatus("NEW");
            lead.setUserId(userId);
            existingClient.ifPresent(lead::setClient);

            Lead saved = leadRepository.save(lead);
            logger.info("Lead saved successfully with ID: {}", saved.getId());

            emailService.sendLeadNotification(saved);

            return saved;
        } catch (Exception e) {
            logger.error("Error creating lead: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create lead: " + e.getMessage(), e);
        }
    }

    public List<Lead> getAllLeads() {
        try {
            return leadRepository.findAllByOrderByCreatedAtDesc();
        } catch (Exception e) {
            logger.error("Error fetching all leads: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch leads: " + e.getMessage(), e);
        }
    }

    public List<Lead> getLeadsByUser(Long userId) {
        return leadRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public Lead updateLead(Long id, Map<String, String> fields) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        if (fields.containsKey("status"))      lead.setStatus(fields.get("status"));
        if (fields.containsKey("comment"))     lead.setComment(fields.get("comment"));
        if (fields.containsKey("serviceType")) lead.setServiceType(fields.get("serviceType"));
        if (fields.containsKey("firstName"))   lead.setFirstName(fields.get("firstName"));
        if (fields.containsKey("lastName"))    lead.setLastName(fields.get("lastName"));
        if (fields.containsKey("phone"))       lead.setPhone(fields.get("phone"));
        if (fields.containsKey("email"))       lead.setEmail(fields.get("email"));
        lead.setUpdatedAt(LocalDateTime.now());
        if ("CLOSED".equals(fields.get("status")) || "CONVERTED".equals(fields.get("status"))) {
            lead.setClosedAt(LocalDateTime.now());
        }
        return leadRepository.save(lead);
    }
}