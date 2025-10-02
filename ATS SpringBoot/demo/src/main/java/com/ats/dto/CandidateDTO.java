package com.ats.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CandidateDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Size(max = 200, message = "LinkedIn URL cannot exceed 200 characters")
    private String linkedinUrl;

    @Size(max = 1000, message = "Skills cannot exceed 1000 characters")
    private String skills;

    private Integer experienceYears;

    @Size(max = 100, message = "Current job title cannot exceed 100 characters")
    private String currentJobTitle;

    @Size(max = 100, message = "Current company cannot exceed 100 characters")
    private String currentCompany;

    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    private String summary;

    @Size(max = 200, message = "Resume URL cannot exceed 200 characters")
    private String resumeUrl;

    public CandidateDTO() {}

    public CandidateDTO(Long id, String firstName, String lastName, String email,
                       String phone, String location, String linkedinUrl, String skills,
                       Integer experienceYears, String currentJobTitle, String currentCompany,
                       String summary, String resumeUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.linkedinUrl = linkedinUrl;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.currentJobTitle = currentJobTitle;
        this.currentCompany = currentCompany;
        this.summary = summary;
        this.resumeUrl = resumeUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getCurrentJobTitle() {
        return currentJobTitle;
    }

    public void setCurrentJobTitle(String currentJobTitle) {
        this.currentJobTitle = currentJobTitle;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}