package com.ats.controller;

import com.ats.entity.Job;
import com.ats.service.JobService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Job> jobPage = jobService.getAllJobs(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("jobs", jobPage.getContent());
            response.put("currentPage", jobPage.getNumber());
            response.put("totalItems", jobPage.getTotalElements());
            response.put("totalPages", jobPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            Optional<Job> job = jobService.getJobById(id);
            if (job.isPresent()) {
                return ResponseEntity.ok(job.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Job not found with id: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> createJob(@Valid @RequestBody Job job) {
        try {
            Job savedJob = jobService.createJob(job);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedJob);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @Valid @RequestBody Job jobDetails) {
        try {
            Job updatedJob = jobService.updateJob(id, jobDetails);
            return ResponseEntity.ok(updatedJob);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Job deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchJobs(@RequestParam String q) {
        try {
            List<Job> jobs = jobService.searchJobs(q);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Search failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-status")
    public ResponseEntity<?> getJobsByStatus(@RequestParam Job.JobStatus status) {
        try {
            List<Job> jobs = jobService.getJobsByStatus(status);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs by status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-priority")
    public ResponseEntity<?> getJobsByPriority(@RequestParam Job.Priority priority) {
        try {
            List<Job> jobs = jobService.getJobsByPriority(priority);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs by priority: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<?> getJobsByCompany(@PathVariable Long companyId) {
        try {
            List<Job> jobs = jobService.getJobsByCompany(companyId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs by company: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-recruiter/{recruiterId}")
    public ResponseEntity<?> getJobsByRecruiter(@PathVariable Long recruiterId) {
        try {
            List<Job> jobs = jobService.getJobsByAssignedRecruiter(recruiterId);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs by recruiter: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-type")
    public ResponseEntity<?> getJobsByType(@RequestParam Job.JobType jobType) {
        try {
            List<Job> jobs = jobService.getJobsByJobType(jobType);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs by type: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-location")
    public ResponseEntity<?> getJobsByLocation(@RequestParam String location) {
        try {
            List<Job> jobs = jobService.getJobsByLocation(location);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs by location: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-salary-range")
    public ResponseEntity<?> getJobsBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary) {
        try {
            List<Job> jobs = jobService.getJobsBySalaryRange(minSalary, maxSalary);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs by salary range: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/upcoming-deadlines")
    public ResponseEntity<?> getJobsWithUpcomingDeadlines(@RequestParam(defaultValue = "7") int days) {
        try {
            List<Job> jobs = jobService.getJobsWithUpcomingDeadlines(days);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch jobs with upcoming deadlines: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/expired")
    public ResponseEntity<?> getExpiredJobs() {
        try {
            List<Job> jobs = jobService.getExpiredJobs();
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch expired jobs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentJobs() {
        try {
            List<Job> jobs = jobService.getRecentJobs(10);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch recent jobs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalJobsCount() {
        try {
            long count = jobService.getTotalJobsCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get jobs count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count/status")
    public ResponseEntity<?> getJobsCountByStatus(@RequestParam Job.JobStatus status) {
        try {
            long count = jobService.getJobsCountByStatus(status);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            response.put("status", Long.valueOf(status.ordinal()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get jobs count by status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count/company/{companyId}")
    public ResponseEntity<?> getJobsCountByCompany(@PathVariable Long companyId) {
        try {
            long count = jobService.getJobsCountByCompany(companyId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            response.put("companyId", companyId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get jobs count by company: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}