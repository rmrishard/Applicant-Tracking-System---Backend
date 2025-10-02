package com.ats.dto;

import com.ats.entity.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class JobDTO {

    private Long id;

    @NotBlank(message = "Job title is required")
    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(max = 2000, message = "Job description cannot exceed 2000 characters")
    private String description;

    @Size(max = 1000, message = "Requirements cannot exceed 1000 characters")
    private String requirements;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private Job.JobType jobType = Job.JobType.FULL_TIME;

    private Job.JobStatus status = Job.JobStatus.OPEN;

    private Job.Priority priority = Job.Priority.MEDIUM;

    private BigDecimal minSalary;

    private BigDecimal maxSalary;

    private LocalDate deadline;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private String companyName;

    private Long assignedRecruiterId;

    private String assignedRecruiterName;

    public JobDTO() {}

    public JobDTO(Long id, String title, String description, String requirements,
                 String location, Job.JobType jobType, Job.JobStatus status,
                 Job.Priority priority, BigDecimal minSalary, BigDecimal maxSalary,
                 LocalDate deadline, Long companyId, String companyName,
                 Long assignedRecruiterId, String assignedRecruiterName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.location = location;
        this.jobType = jobType;
        this.status = status;
        this.priority = priority;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.deadline = deadline;
        this.companyId = companyId;
        this.companyName = companyName;
        this.assignedRecruiterId = assignedRecruiterId;
        this.assignedRecruiterName = assignedRecruiterName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Job.JobType getJobType() {
        return jobType;
    }

    public void setJobType(Job.JobType jobType) {
        this.jobType = jobType;
    }

    public Job.JobStatus getStatus() {
        return status;
    }

    public void setStatus(Job.JobStatus status) {
        this.status = status;
    }

    public Job.Priority getPriority() {
        return priority;
    }

    public void setPriority(Job.Priority priority) {
        this.priority = priority;
    }

    public BigDecimal getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(BigDecimal minSalary) {
        this.minSalary = minSalary;
    }

    public BigDecimal getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(BigDecimal maxSalary) {
        this.maxSalary = maxSalary;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getAssignedRecruiterId() {
        return assignedRecruiterId;
    }

    public void setAssignedRecruiterId(Long assignedRecruiterId) {
        this.assignedRecruiterId = assignedRecruiterId;
    }

    public String getAssignedRecruiterName() {
        return assignedRecruiterName;
    }

    public void setAssignedRecruiterName(String assignedRecruiterName) {
        this.assignedRecruiterName = assignedRecruiterName;
    }

    public String getSalaryRange() {
        if (minSalary != null && maxSalary != null) {
            return "$" + minSalary + " - $" + maxSalary;
        } else if (minSalary != null) {
            return "$" + minSalary + "+";
        } else if (maxSalary != null) {
            return "Up to $" + maxSalary;
        }
        return "Salary not specified";
    }
}