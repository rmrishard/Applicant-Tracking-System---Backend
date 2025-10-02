package com.ats.controller;

import com.ats.entity.Candidate;
import com.ats.service.CandidateService;
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
@RequestMapping("/api/candidates")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Candidate> candidatePage = candidateService.getAllCandidates(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("candidates", candidatePage.getContent());
            response.put("currentPage", candidatePage.getNumber());
            response.put("totalItems", candidatePage.getTotalElements());
            response.put("totalPages", candidatePage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCandidateById(@PathVariable Long id) {
        try {
            Optional<Candidate> candidate = candidateService.getCandidateById(id);
            if (candidate.isPresent()) {
                return ResponseEntity.ok(candidate.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Candidate not found with id: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidate: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> createCandidate(@Valid @RequestBody Candidate candidate) {
        try {
            if (candidateService.existsByEmail(candidate.getEmail())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Candidate with this email already exists");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Candidate savedCandidate = candidateService.createCandidate(candidate);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create candidate: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> updateCandidate(@PathVariable Long id, @Valid @RequestBody Candidate candidateDetails) {
        try {
            Candidate updatedCandidate = candidateService.updateCandidate(id, candidateDetails);
            return ResponseEntity.ok(updatedCandidate);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update candidate: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        try {
            candidateService.deleteCandidate(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Candidate deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete candidate: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCandidates(@RequestParam String q) {
        try {
            List<Candidate> candidates = candidateService.searchCandidates(q);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Search failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-skills")
    public ResponseEntity<?> getCandidatesBySkills(@RequestParam String skills) {
        try {
            List<Candidate> candidates = candidateService.searchCandidatesBySkills(skills);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidates by skills: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-location")
    public ResponseEntity<?> getCandidatesByLocation(@RequestParam String location) {
        try {
            List<Candidate> candidates = candidateService.getCandidatesByLocation(location);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidates by location: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-experience")
    public ResponseEntity<?> getCandidatesByExperience(
            @RequestParam Integer minYears,
            @RequestParam Integer maxYears) {
        try {
            List<Candidate> candidates = candidateService.getCandidatesByExperience(minYears, maxYears);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidates by experience: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-company")
    public ResponseEntity<?> getCandidatesByCurrentCompany(@RequestParam String company) {
        try {
            List<Candidate> candidates = candidateService.getCandidatesByCurrentCompany(company);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidates by company: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/with-linkedin")
    public ResponseEntity<?> getCandidatesWithLinkedIn() {
        try {
            List<Candidate> candidates = candidateService.getCandidatesWithLinkedIn();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidates with LinkedIn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/with-resume")
    public ResponseEntity<?> getCandidatesWithResume() {
        try {
            List<Candidate> candidates = candidateService.getCandidatesWithResume();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch candidates with resume: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalCandidatesCount() {
        try {
            long count = candidateService.getTotalCandidatesCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get candidates count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}