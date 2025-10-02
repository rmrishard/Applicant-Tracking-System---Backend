package com.ats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyDTO {

    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Industry cannot exceed 100 characters")
    private String industry;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Size(max = 100, message = "Website cannot exceed 100 characters")
    private String website;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    public CompanyDTO() {}

    public CompanyDTO(Long id, String name, String description, String industry,
                     String location, String website, String phone) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.industry = industry;
        this.location = location;
        this.website = website;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}