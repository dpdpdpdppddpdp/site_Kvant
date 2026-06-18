package com.kvant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LeadRequestDto {
    @NotBlank(message = "Имя обязательно")
    private String firstName;
    
    private String lastName;
    
    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "\\+7\\s\\(\\d{3}\\)\\s\\d{3}-\\d{2}-\\d{2}")
    private String phone;
    
    @NotBlank(message = "Email обязателен")
    @Email
    private String email;
    
    @NotBlank(message = "Укажите тип услуги")
    private String serviceType;
    
    private String comment;
    private String utmSource;
    private String utmCampaign;
    private String utmMedium;
    private String utmContent;
    private String referrerUrl;

    public LeadRequestDto() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getUtmSource() { return utmSource; }
    public void setUtmSource(String utmSource) { this.utmSource = utmSource; }
    public String getUtmCampaign() { return utmCampaign; }
    public void setUtmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; }
    public String getUtmMedium() { return utmMedium; }
    public void setUtmMedium(String utmMedium) { this.utmMedium = utmMedium; }
    public String getUtmContent() { return utmContent; }
    public void setUtmContent(String utmContent) { this.utmContent = utmContent; }
    public String getReferrerUrl() { return referrerUrl; }
    public void setReferrerUrl(String referrerUrl) { this.referrerUrl = referrerUrl; }
}