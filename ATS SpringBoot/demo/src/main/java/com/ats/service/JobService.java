package com.ats.service;

import com.ats.entity.Job;
import com.ats.entity.User;
import com.ats.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Page<Job> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public Job createJob(Job job) {
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job jobDetails) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setRequirements(jobDetails.getRequirements());
        job.setLocation(jobDetails.getLocation());
        job.setJobType(jobDetails.getJobType());
        job.setMinSalary(jobDetails.getMinSalary());
        job.setMaxSalary(jobDetails.getMaxSalary());
        job.setStatus(jobDetails.getStatus());
        job.setPriority(jobDetails.getPriority());
        job.setDeadline(jobDetails.getDeadline());
        job.setCompany(jobDetails.getCompany());
        job.setAssignedRecruiter(jobDetails.getAssignedRecruiter());
        job.setUpdatedAt(LocalDateTime.now());

        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
        jobRepository.delete(job);
    }

    public List<Job> getJobsByStatus(Job.JobStatus status) {
        return jobRepository.findByStatus(status);
    }

    public List<Job> getJobsByPriority(Job.Priority priority) {
        return jobRepository.findByPriority(priority);
    }

    public List<Job> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyId(companyId);
    }

    public List<Job> getJobsByAssignedRecruiter(Long recruiterId) {
        return jobRepository.findByAssignedRecruiterId(recruiterId);
    }

    public List<Job> getJobsByJobType(Job.JobType jobType) {
        return jobRepository.findByJobType(jobType);
    }

    public List<Job> searchJobs(String query) {
        return jobRepository.findBySearchTerm(query);
    }

    public List<Job> getJobsByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Job> getJobsBySalaryRange(Double minSalary, Double maxSalary) {
        return jobRepository.findBySalaryRange(minSalary, maxSalary);
    }

    public List<Job> getJobsWithUpcomingDeadlines(int days) {
        LocalDate endDate = LocalDate.now().plusDays(days);
        return jobRepository.findByDeadlineBetween(LocalDate.now(), endDate);
    }

    public List<Job> getExpiredJobs() {
        return jobRepository.findByDeadlineBefore(LocalDate.now());
    }

    public List<Job> getJobsByStatusAndPriority(Job.JobStatus status, Job.Priority priority) {
        return jobRepository.findByStatusAndPriority(status, priority);
    }

    public List<Job> getJobsByStatusAndCompany(Job.JobStatus status, Long companyId) {
        return jobRepository.findByStatusAndCompanyId(status, companyId);
    }

    public long getTotalJobsCount() {
        return jobRepository.count();
    }

    public long getJobsCountByStatus(Job.JobStatus status) {
        return jobRepository.countByStatus(status);
    }

    public long getJobsCountByCompany(Long companyId) {
        return jobRepository.countByCompanyId(companyId);
    }

    public long getJobsCountByRecruiter(Long recruiterId) {
        return jobRepository.countByAssignedRecruiterId(recruiterId);
    }

    public List<Job> getRecentJobs(int limit) {
        return jobRepository.findTopByOrderByCreatedAtDesc();
    }
}