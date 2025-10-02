package com.ats.controller;

import com.ats.entity.Application;
import com.ats.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Application> applicationPage = applicationService.getAllApplications(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("applications", applicationPage.getContent());
            response.put("currentPage", applicationPage.getNumber());
            response.put("totalItems", applicationPage.getTotalElements());
            response.put("totalPages", applicationPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            Optional<Application> application = applicationService.getApplicationById(id);
            if (application.isPresent()) {
                return ResponseEntity.ok(application.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Application not found with id: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> createApplication(@Valid @RequestBody Application application) {
        try {
            Application savedApplication = applicationService.createApplication(application);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedApplication);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/candidate/{candidateId}/job/{jobId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> createApplicationForCandidateAndJob(
            @PathVariable Long candidateId,
            @PathVariable Long jobId) {
        try {
            Application savedApplication = applicationService.createApplication(candidateId, jobId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedApplication);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> updateApplication(@PathVariable Long id, @Valid @RequestBody Application applicationDetails) {
        try {
            Application updatedApplication = applicationService.updateApplication(id, applicationDetails);
            return ResponseEntity.ok(updatedApplication);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam Application.ApplicationStatus status) {
        try {
            Application updatedApplication = applicationService.updateApplicationStatus(id, status);
            return ResponseEntity.ok(updatedApplication);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update application status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        try {
            applicationService.deleteApplication(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Application deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicationsByJob(@PathVariable Long jobId) {
        try {
            List<Application> applications = applicationService.getApplicationsByJob(jobId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications by job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<?> getApplicationsByCandidate(@PathVariable Long candidateId) {
        try {
            List<Application> applications = applicationService.getApplicationsByCandidate(candidateId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications by candidate: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-status")
    public ResponseEntity<?> getApplicationsByStatus(@RequestParam Application.ApplicationStatus status) {
        try {
            List<Application> applications = applicationService.getApplicationsByStatus(status);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications by status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/follow-up")
    public ResponseEntity<?> getApplicationsRequiringFollowUp() {
        try {
            List<Application> applications = applicationService.getApplicationsRequiringFollowUp();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications requiring follow-up: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/follow-up/by-date")
    public ResponseEntity<?> getApplicationsRequiringFollowUpByDate(@RequestParam String date) {
        try {
            LocalDateTime followUpDate = LocalDateTime.parse(date);
            List<Application> applications = applicationService.getApplicationsRequiringFollowUp(followUpDate);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications requiring follow-up by date: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/job/{jobId}/status")
    public ResponseEntity<?> getApplicationsByJobAndStatus(
            @PathVariable Long jobId,
            @RequestParam Application.ApplicationStatus status) {
        try {
            List<Application> applications = applicationService.getApplicationsByJobAndStatus(jobId, status);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications by job and status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentApplications() {
        try {
            List<Application> applications = applicationService.getRecentApplications(10);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch recent applications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/high-rated")
    public ResponseEntity<?> getHighRatedApplications(@RequestParam(defaultValue = "4") Integer minRating) {
        try {
            List<Application> applications = applicationService.getHighRatedApplications(minRating);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch high-rated applications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/without-follow-up")
    public ResponseEntity<?> getApplicationsWithoutFollowUp() {
        try {
            List<Application> applications = applicationService.getApplicationsWithoutFollowUp();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch applications without follow-up: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalApplicationsCount() {
        try {
            long count = applicationService.getTotalApplicationsCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get applications count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count/status")
    public ResponseEntity<?> getApplicationsCountByStatus(@RequestParam Application.ApplicationStatus status) {
        try {
            long count = applicationService.getApplicationsCountByStatus(status);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            response.put("status", Long.valueOf(status.ordinal()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get applications count by status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count/job/{jobId}")
    public ResponseEntity<?> getApplicationsCountByJob(@PathVariable Long jobId) {
        try {
            long count = applicationService.getApplicationsCountByJob(jobId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            response.put("jobId", jobId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get applications count by job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/exists/candidate/{candidateId}/job/{jobId}")
    public ResponseEntity<?> checkApplicationExists(
            @PathVariable Long candidateId,
            @PathVariable Long jobId) {
        try {
            boolean exists = applicationService.existsApplicationByCandidateAndJob(candidateId, jobId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to check application existence: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}