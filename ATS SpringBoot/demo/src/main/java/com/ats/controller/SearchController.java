package com.ats.controller;

import com.ats.dto.ApiResponse;
import com.ats.entity.Candidate;
import com.ats.entity.Job;
import com.ats.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/global")
    public ResponseEntity<ApiResponse<Map<String, Object>>> globalSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> results = searchService.globalSearch(q, limit);
            return ResponseEntity.ok(ApiResponse.success("Global search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Global search failed", e.getMessage()));
        }
    }

    @GetMapping("/ranked")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rankedSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            Map<String, Object> results = searchService.searchWithRanking(q, limit);
            return ResponseEntity.ok(ApiResponse.success("Ranked search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Ranked search failed", e.getMessage()));
        }
    }

    @GetMapping("/candidates")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchCandidates(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Map<String, Object>> results = searchService.searchCandidates(q, limit);
            return ResponseEntity.ok(ApiResponse.success("Candidate search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Candidate search failed", e.getMessage()));
        }
    }

    @GetMapping("/candidates/advanced")
    public ResponseEntity<ApiResponse<Page<Candidate>>> searchCandidatesAdvanced(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) String skills,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Candidate> results = searchService.searchCandidatesAdvanced(
                    q, location, minExperience, maxExperience, skills, pageable);
            return ResponseEntity.ok(ApiResponse.success("Advanced candidate search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Advanced candidate search failed", e.getMessage()));
        }
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchCompanies(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Map<String, Object>> results = searchService.searchCompanies(q, limit);
            return ResponseEntity.ok(ApiResponse.success("Company search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Company search failed", e.getMessage()));
        }
    }

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchJobs(
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Map<String, Object>> results = searchService.searchJobs(q, limit);
            return ResponseEntity.ok(ApiResponse.success("Job search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Job search failed", e.getMessage()));
        }
    }

    @GetMapping("/jobs/advanced")
    public ResponseEntity<ApiResponse<Page<Job>>> searchJobsAdvanced(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Job.JobType jobType,
            @RequestParam(required = false) Job.JobStatus status,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Job> results = searchService.searchJobsAdvanced(
                    q, location, jobType, status, minSalary, maxSalary, companyId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Advanced job search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Advanced job search failed", e.getMessage()));
        }
    }

    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchByFilters(
            @RequestBody Map<String, Object> filters,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<Map<String, Object>> results = searchService.searchByFilters(filters, limit);
            return ResponseEntity.ok(ApiResponse.success("Filtered search completed successfully", results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Filtered search failed", e.getMessage()));
        }
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSearchSuggestions(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            // Basic suggestions based on limited results from each category
            Map<String, Object> suggestions = searchService.globalSearch(q, limit);
            return ResponseEntity.ok(ApiResponse.success("Search suggestions retrieved successfully", suggestions));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get search suggestions", e.getMessage()));
        }
    }
}