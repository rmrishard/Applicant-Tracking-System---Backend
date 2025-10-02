package com.ats.service;

import com.ats.entity.Application;
import com.ats.entity.Candidate;
import com.ats.entity.Job;
import com.ats.repository.ApplicationRepository;
import com.ats.repository.CandidateRepository;
import com.ats.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Page<Application> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public Application createApplication(Long candidateId, Long jobId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        // Check if application already exists
        if (applicationRepository.existsByCandidateIdAndJobId(candidateId, jobId)) {
            throw new RuntimeException("Application already exists for this candidate and job");
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setStatus(Application.ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public Application createApplication(Application application) {
        // Check if application already exists
        if (applicationRepository.existsByCandidateIdAndJobId(
                application.getCandidate().getId(),
                application.getJob().getId())) {
            throw new RuntimeException("Application already exists for this candidate and job");
        }

        application.setAppliedAt(LocalDateTime.now());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        if (application.getStatus() == null) {
            application.setStatus(Application.ApplicationStatus.APPLIED);
        }

        return applicationRepository.save(application);
    }

    public Application updateApplication(Long id, Application applicationDetails) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));

        application.setStatus(applicationDetails.getStatus());
        application.setRating(applicationDetails.getRating());
        application.setFollowUpDate(applicationDetails.getFollowUpDate());
        application.setLastContactDate(applicationDetails.getLastContactDate());
        application.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public Application updateApplicationStatus(Long id, Application.ApplicationStatus status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));

        application.setStatus(status);
        application.setLastContactDate(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
        applicationRepository.delete(application);
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<Application> getApplicationsByCandidate(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    public List<Application> getApplicationsByStatus(Application.ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }

    public List<Application> getApplicationsRequiringFollowUp() {
        return applicationRepository.findByFollowUpDateBeforeAndStatusNot(
                LocalDateTime.now(),
                Application.ApplicationStatus.REJECTED
        );
    }

    public List<Application> getApplicationsRequiringFollowUp(LocalDateTime date) {
        return applicationRepository.findByFollowUpDateBeforeAndStatusNot(
                date,
                Application.ApplicationStatus.REJECTED
        );
    }

    public List<Application> getApplicationsByJobAndStatus(Long jobId, Application.ApplicationStatus status) {
        return applicationRepository.findByJobIdAndStatus(jobId, status);
    }

    public List<Application> getApplicationsByCandidateAndStatus(Long candidateId, Application.ApplicationStatus status) {
        return applicationRepository.findByCandidateIdAndStatus(candidateId, status);
    }

    public List<Application> getRecentApplications(int limit) {
        return applicationRepository.findTopByOrderByAppliedAtDesc();
    }

    public long getTotalApplicationsCount() {
        return applicationRepository.count();
    }

    public long getApplicationsCountByStatus(Application.ApplicationStatus status) {
        return applicationRepository.countByStatus(status);
    }

    public long getApplicationsCountByJob(Long jobId) {
        return applicationRepository.countByJobId(jobId);
    }

    public long getApplicationsCountByCandidate(Long candidateId) {
        return applicationRepository.countByCandidateId(candidateId);
    }

    public List<Application> getApplicationsByRatingRange(Integer minRating, Integer maxRating) {
        return applicationRepository.findByRatingBetween(minRating, maxRating);
    }

    public List<Application> getHighRatedApplications(Integer minRating) {
        return applicationRepository.findByRatingGreaterThanEqual(minRating);
    }

    public Optional<Application> getApplicationByCandidateAndJob(Long candidateId, Long jobId) {
        return applicationRepository.findByCandidateIdAndJobId(candidateId, jobId);
    }

    public boolean existsApplicationByCandidateAndJob(Long candidateId, Long jobId) {
        return applicationRepository.existsByCandidateIdAndJobId(candidateId, jobId);
    }

    public List<Application> getApplicationsWithoutFollowUp() {
        return applicationRepository.findByFollowUpDateIsNullAndStatusIn(
            List.of(Application.ApplicationStatus.APPLIED,
                   Application.ApplicationStatus.SCREENING,
                   Application.ApplicationStatus.INTERVIEWING)
        );
    }
}