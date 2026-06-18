package com.kvant.service;

import com.kvant.entity.Lead;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final ExcelService excelService;
    private final String adminEmail;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender, ExcelService excelService,
                        @Value("${admin.email}") String adminEmail,
                        @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.excelService = excelService;
        this.adminEmail = adminEmail;
        this.fromEmail = fromEmail;
    }

    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "Kvant");
            helper.setTo(toEmail);
            helper.setSubject("Код подтверждения регистрации — Kvant");
            helper.setText(
                "<html><body style='font-family:sans-serif;max-width:480px;margin:0 auto;padding:32px;'>" +
                "<h2 style='color:#2563eb;margin-bottom:8px;'>Kvant</h2>" +
                "<p style='color:#475569;margin-bottom:24px;'>Подтвердите ваш email для завершения регистрации</p>" +
                "<div style='background:#f1f5f9;border-radius:12px;padding:24px;text-align:center;margin-bottom:24px;'>" +
                "<p style='color:#64748b;font-size:0.9rem;margin-bottom:8px;'>Ваш код:</p>" +
                "<div style='font-size:2.5rem;font-weight:700;letter-spacing:12px;color:#1e293b;'>" + code + "</div>" +
                "</div>" +
                "<p style='color:#94a3b8;font-size:0.8rem;'>Код действителен 10 минут. Если вы не регистрировались — проигнорируйте это письмо.</p>" +
                "</body></html>", true);
            mailSender.send(message);
            logger.info("Verification code sent to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send verification code to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить письмо: " + e.getMessage(), e);
        }
    }

    @Async
    public void sendLeadNotification(Lead lead) {
        try {
            byte[] excel = excelService.generateLeadExcel(lead);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "Kvant");
            helper.setTo(adminEmail);
            helper.setSubject("Новая заявка от " + lead.getFirstName());
            helper.setText(buildEmailText(lead), true);
            helper.addAttachment("lead_" + lead.getId() + ".xlsx", new ByteArrayResource(excel));
            mailSender.send(message);
            logger.info("Email notification sent for lead ID: {}", lead.getId());
        } catch (Exception e) {
            logger.error("Failed to send email notification for lead ID: {}. Error: {}", lead.getId(), e.getMessage(), e);
        }
    }

    private String buildEmailText(Lead lead) {
        return "<html><body>" +
               "<h2>Новая заявка с сайта</h2>" +
               "<p><strong>Имя:</strong> " + lead.getFirstName() + (lead.getLastName() != null ? " " + lead.getLastName() : "") + "</p>" +
               "<p><strong>Телефон:</strong> " + lead.getPhone() + "</p>" +
               "<p><strong>Email:</strong> " + lead.getEmail() + "</p>" +
               "<p><strong>Услуга:</strong> " + lead.getServiceType() + "</p>" +
               "<p><strong>Комментарий:</strong> " + (lead.getComment() != null ? lead.getComment() : "") + "</p>" +
               "<p><strong>Источник:</strong> " + (lead.getUtmSource() != null ? lead.getUtmSource() : "прямой заход") + "</p>" +
               "</body></html>";
    }
}