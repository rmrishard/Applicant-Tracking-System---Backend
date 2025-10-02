package com.ats.service;

import com.ats.entity.Application;
import com.ats.entity.Candidate;
import com.ats.entity.Company;
import com.ats.entity.Job;
import com.ats.repository.ApplicationRepository;
import com.ats.repository.CandidateRepository;
import com.ats.repository.CompanyRepository;
import com.ats.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public Map<String, Object> globalSearch(String query, int limit) {
        Map<String, Object> results = new HashMap<>();

        results.put("candidates", searchCandidates(query, limit));
        results.put("companies", searchCompanies(query, limit));
        results.put("jobs", searchJobs(query, limit));

        return results;
    }

    public List<Map<String, Object>> searchCandidates(String query, int limit) {
        List<Candidate> candidates = candidateRepository.findBySearchTerm(query);

        return candidates.stream()
                .limit(limit)
                .map(candidate -> {
                    Map<String, Object> candidateMap = new HashMap<>();
                    candidateMap.put("id", candidate.getId());
                    candidateMap.put("name", candidate.getFullName());
                    candidateMap.put("email", candidate.getEmail());
                    candidateMap.put("phone", candidate.getPhone());
                    candidateMap.put("location", candidate.getLocation());
                    candidateMap.put("currentJobTitle", candidate.getCurrentJobTitle());
                    candidateMap.put("experienceYears", candidate.getExperienceYears());
                    candidateMap.put("skills", candidate.getSkills());
                    candidateMap.put("type", "candidate");
                    return candidateMap;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchCompanies(String query, int limit) {
        List<Company> companies = companyRepository.findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCase(query, query);

        return companies.stream()
                .limit(limit)
                .map(company -> {
                    Map<String, Object> companyMap = new HashMap<>();
                    companyMap.put("id", company.getId());
                    companyMap.put("name", company.getName());
                    companyMap.put("industry", company.getIndustry());
                    companyMap.put("location", company.getLocation());
                    companyMap.put("website", company.getWebsite());
                    companyMap.put("description", company.getDescription());
                    companyMap.put("type", "company");
                    return companyMap;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchJobs(String query, int limit) {
        List<Job> jobs = jobRepository.findBySearchTerm(query);

        return jobs.stream()
                .limit(limit)
                .map(job -> {
                    Map<String, Object> jobMap = new HashMap<>();
                    jobMap.put("id", job.getId());
                    jobMap.put("title", job.getTitle());
                    jobMap.put("companyName", job.getCompany().getName());
                    jobMap.put("location", job.getLocation());
                    jobMap.put("jobType", job.getJobType());
                    jobMap.put("status", job.getStatus());
                    jobMap.put("priority", job.getPriority());
                    jobMap.put("deadline", job.getDeadline());
                    jobMap.put("type", "job");
                    return jobMap;
                })
                .collect(Collectors.toList());
    }

    public Page<Candidate> searchCandidatesAdvanced(
            String query, String location, Integer minExperience, Integer maxExperience,
            String skills, Pageable pageable) {

        if (query == null) query = "";

        return candidateRepository.findByAdvancedSearch(
                query, location, minExperience, maxExperience, skills, pageable);
    }

    public Page<Job> searchJobsAdvanced(
            String query, String location, Job.JobType jobType, Job.JobStatus status,
            BigDecimal minSalary, BigDecimal maxSalary, Long companyId, Pageable pageable) {

        if (query == null) query = "";

        return jobRepository.findByAdvancedSearch(
                query, location, jobType, status, minSalary, maxSalary, companyId, pageable);
    }

    public List<Map<String, Object>> searchByFilters(Map<String, Object> filters, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();

        String query = (String) filters.get("query");
        String type = (String) filters.get("type");

        if (type == null || "candidate".equalsIgnoreCase(type)) {
            results.addAll(searchCandidates(query != null ? query : "", limit));
        }

        if (type == null || "company".equalsIgnoreCase(type)) {
            results.addAll(searchCompanies(query != null ? query : "", limit));
        }

        if (type == null || "job".equalsIgnoreCase(type)) {
            results.addAll(searchJobs(query != null ? query : "", limit));
        }

        return results.stream()
                .sorted((a, b) -> {
                    String aType = (String) a.get("type");
                    String bType = (String) b.get("type");
                    return aType.compareTo(bType);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map<String, Object> searchWithRanking(String query, int limit) {
        Map<String, Object> results = globalSearch(query, limit * 2);

        List<Map<String, Object>> rankedResults = new ArrayList<>();

        // Add candidates with relevance scoring
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) results.get("candidates");
        candidates.forEach(candidate -> {
            double score = calculateRelevanceScore(query, candidate);
            candidate.put("relevanceScore", score);
            rankedResults.add(candidate);
        });

        // Add companies with relevance scoring
        List<Map<String, Object>> companies = (List<Map<String, Object>>) results.get("companies");
        companies.forEach(company -> {
            double score = calculateRelevanceScore(query, company);
            company.put("relevanceScore", score);
            rankedResults.add(company);
        });

        // Add jobs with relevance scoring
        List<Map<String, Object>> jobs = (List<Map<String, Object>>) results.get("jobs");
        jobs.forEach(job -> {
            double score = calculateRelevanceScore(query, job);
            job.put("relevanceScore", score);
            rankedResults.add(job);
        });

        // Sort by relevance score and limit results
        rankedResults.sort((a, b) ->
                Double.compare((Double) b.get("relevanceScore"), (Double) a.get("relevanceScore")));

        Map<String, Object> rankedResponse = new HashMap<>();
        rankedResponse.put("query", query);
        rankedResponse.put("totalResults", rankedResults.size());
        rankedResponse.put("results", rankedResults.stream().limit(limit).collect(Collectors.toList()));

        return rankedResponse;
    }

    private double calculateRelevanceScore(String query, Map<String, Object> item) {
        if (query == null || query.trim().isEmpty()) {
            return 1.0;
        }

        double score = 0.0;
        String lowerQuery = query.toLowerCase();

        // Check different fields based on item type
        String type = (String) item.get("type");

        switch (type) {
            case "candidate":
                score += checkFieldMatch(lowerQuery, (String) item.get("name"), 3.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("skills"), 2.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("currentJobTitle"), 2.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("location"), 1.0);
                break;
            case "company":
                score += checkFieldMatch(lowerQuery, (String) item.get("name"), 3.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("industry"), 2.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("description"), 1.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("location"), 1.0);
                break;
            case "job":
                score += checkFieldMatch(lowerQuery, (String) item.get("title"), 3.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("companyName"), 2.0);
                score += checkFieldMatch(lowerQuery, (String) item.get("location"), 1.0);
                break;
        }

        return score;
    }

    private double checkFieldMatch(String query, String field, double weight) {
        if (field == null) return 0.0;

        String lowerField = field.toLowerCase();
        if (lowerField.contains(query)) {
            // Exact match gets full weight
            if (lowerField.equals(query)) {
                return weight * 2;
            }
            // Starts with gets higher score
            if (lowerField.startsWith(query)) {
                return weight * 1.5;
            }
            // Contains gets base weight
            return weight;
        }
        return 0.0;
    }
}