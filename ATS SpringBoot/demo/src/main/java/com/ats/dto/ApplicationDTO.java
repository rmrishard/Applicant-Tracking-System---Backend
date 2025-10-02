package com.ats.dto;

import com.ats.entity.Application;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ApplicationDTO {

    private Long id;

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    private String candidateName;

    private String candidateEmail;

    @NotNull(message = "Job ID is required")
    private Long jobId;

    private String jobTitle;

    private String companyName;

    private Application.ApplicationStatus status = Application.ApplicationStatus.APPLIED;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    private LocalDateTime appliedAt;

    private LocalDateTime lastContactDate;

    private LocalDateTime followUpDate;

    public ApplicationDTO() {}

    public ApplicationDTO(Long id, Long candidateId, String candidateName, String candidateEmail,
                         Long jobId, String jobTitle, String companyName,
                         Application.ApplicationStatus status, Integer rating,
                         LocalDateTime appliedAt, LocalDateTime lastContactDate,
                         LocalDateTime followUpDate) {
        this.id = id;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.status = status;
        this.rating = rating;
        this.appliedAt = appliedAt;
        this.lastContactDate = lastContactDate;
        this.followUpDate = followUpDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Application.ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(Application.ApplicationStatus status) {
        this.status = status;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getLastContactDate() {
        return lastContactDate;
    }

    public void setLastContactDate(LocalDateTime lastContactDate) {
        this.lastContactDate = lastContactDate;
    }

    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }

    public boolean isFollowUpNeeded() {
        return followUpDate != null && followUpDate.isBefore(LocalDateTime.now());
    }
}