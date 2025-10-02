package com.ats.service;

import com.ats.entity.Application;
import com.ats.entity.Job;
import com.ats.repository.ApplicationRepository;
import com.ats.repository.CandidateRepository;
import com.ats.repository.CompanyRepository;
import com.ats.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalJobs", jobRepository.count());
        stats.put("totalCandidates", candidateRepository.count());
        stats.put("totalCompanies", companyRepository.count());

        stats.put("openJobs", jobRepository.countByStatus(Job.JobStatus.OPEN));
        stats.put("filledJobs", jobRepository.countByStatus(Job.JobStatus.FILLED));

        stats.put("activeApplications", applicationRepository.countByStatusNot(Application.ApplicationStatus.REJECTED));
        stats.put("hiredCandidates", applicationRepository.countByStatus(Application.ApplicationStatus.HIRED));

        return stats;
    }

    public List<Map<String, Object>> getApplicationsByStatus() {
        List<Map<String, Object>> statusStats = new ArrayList<>();

        for (Application.ApplicationStatus status : Application.ApplicationStatus.values()) {
            Map<String, Object> statusStat = new HashMap<>();
            statusStat.put("status", status.name());
            statusStat.put("count", applicationRepository.countByStatus(status));
            statusStats.add(statusStat);
        }

        return statusStats;
    }

    public List<Map<String, Object>> getJobsByStatus() {
        List<Map<String, Object>> statusStats = new ArrayList<>();

        for (Job.JobStatus status : Job.JobStatus.values()) {
            Map<String, Object> statusStat = new HashMap<>();
            statusStat.put("status", status.name());
            statusStat.put("count", jobRepository.countByStatus(status));
            statusStats.add(statusStat);
        }

        return statusStats;
    }

    public List<Map<String, Object>> getApplicationsPerJob() {
        List<Object[]> results = applicationRepository.getApplicationCountPerJob();

        return results.stream().map(result -> {
            Map<String, Object> jobStat = new HashMap<>();
            jobStat.put("jobTitle", result[0]);
            jobStat.put("companyName", result[1]);
            jobStat.put("applicationCount", result[2]);
            return jobStat;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getConversionRates() {
        Map<String, Object> rates = new HashMap<>();

        long totalApplications = applicationRepository.count();
        if (totalApplications == 0) {
            return rates;
        }

        long screeningCount = applicationRepository.countByStatus(Application.ApplicationStatus.SCREENING);
        long interviewingCount = applicationRepository.countByStatus(Application.ApplicationStatus.INTERVIEWING);
        long offerCount = applicationRepository.countByStatus(Application.ApplicationStatus.OFFER);
        long hiredCount = applicationRepository.countByStatus(Application.ApplicationStatus.HIRED);

        rates.put("screeningRate", (double) screeningCount / totalApplications * 100);
        rates.put("interviewRate", (double) interviewingCount / totalApplications * 100);
        rates.put("offerRate", (double) offerCount / totalApplications * 100);
        rates.put("hireRate", (double) hiredCount / totalApplications * 100);

        return rates;
    }

    public List<Map<String, Object>> getRecentActivity(int limit) {
        List<Application> recentApplications = applicationRepository.findTopByOrderByCreatedAtDesc(limit);

        return recentApplications.stream().map(app -> {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", app.getId());
            activity.put("candidateName", app.getCandidate().getFullName());
            activity.put("jobTitle", app.getJob().getTitle());
            activity.put("companyName", app.getJob().getCompany().getName());
            activity.put("status", app.getStatus().name());
            activity.put("appliedAt", app.getAppliedAt());
            activity.put("lastUpdate", app.getUpdatedAt());
            return activity;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getFollowUpNeeded() {
        LocalDateTime now = LocalDateTime.now();
        List<Application> followUpApplications = applicationRepository.findByFollowUpDateBeforeAndStatusNot(
                now, Application.ApplicationStatus.REJECTED);

        return followUpApplications.stream().map(app -> {
            Map<String, Object> followUp = new HashMap<>();
            followUp.put("id", app.getId());
            followUp.put("candidateName", app.getCandidate().getFullName());
            followUp.put("candidateEmail", app.getCandidate().getEmail());
            followUp.put("jobTitle", app.getJob().getTitle());
            followUp.put("companyName", app.getJob().getCompany().getName());
            followUp.put("status", app.getStatus().name());
            followUp.put("followUpDate", app.getFollowUpDate());
            followUp.put("daysPastDue", ChronoUnit.DAYS.between(app.getFollowUpDate(), now));
            return followUp;
        }).collect(Collectors.toList());
    }

    public double getAverageTimeToFill() {
        List<Application> hiredApplications = applicationRepository.findByStatus(Application.ApplicationStatus.HIRED);

        if (hiredApplications.isEmpty()) {
            return 0.0;
        }

        double totalDays = hiredApplications.stream()
                .mapToLong(app -> ChronoUnit.DAYS.between(app.getAppliedAt(), app.getUpdatedAt()))
                .average()
                .orElse(0.0);

        return totalDays;
    }

    public List<Map<String, Object>> getRecruiterPerformance() {
        List<Object[]> results = applicationRepository.getRecruiterPerformance();

        return results.stream().map(result -> {
            Map<String, Object> performance = new HashMap<>();
            performance.put("recruiterName", result[0] + " " + result[1]);
            performance.put("totalJobs", result[2]);
            performance.put("totalApplications", result[3]);
            performance.put("hiredCount", result[4]);

            long totalApps = (Long) result[3];
            long hired = (Long) result[4];
            performance.put("successRate", totalApps > 0 ? (double) hired / totalApps * 100 : 0.0);

            return performance;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getMonthlyTrends() {
        Map<String, Object> trends = new HashMap<>();

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        List<Object[]> applicationTrends = applicationRepository.getMonthlyApplicationTrends(sixMonthsAgo);
        List<Object[]> hireTrends = applicationRepository.getMonthlyHireTrends(sixMonthsAgo);

        trends.put("applicationTrends", applicationTrends.stream().map(result -> {
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", result[0]);
            trend.put("year", result[1]);
            trend.put("count", result[2]);
            return trend;
        }).collect(Collectors.toList()));

        trends.put("hireTrends", hireTrends.stream().map(result -> {
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", result[0]);
            trend.put("year", result[1]);
            trend.put("count", result[2]);
            return trend;
        }).collect(Collectors.toList()));

        return trends;
    }
}