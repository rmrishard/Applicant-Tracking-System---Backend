package com.ats.controller;

import com.ats.dto.ApiResponse;
import com.ats.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
public class DashboardController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            Map<String, Object> stats = analyticsService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve dashboard statistics", e.getMessage()));
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> activities = analyticsService.getRecentActivity(limit);
            return ResponseEntity.ok(ApiResponse.success("Recent activities retrieved successfully", activities));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve recent activities", e.getMessage()));
        }
    }

    @GetMapping("/follow-ups")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFollowUps() {
        try {
            List<Map<String, Object>> followUps = analyticsService.getFollowUpNeeded();
            return ResponseEntity.ok(ApiResponse.success("Follow-ups retrieved successfully", followUps));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve follow-ups", e.getMessage()));
        }
    }

    @GetMapping("/applications-by-status")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getApplicationsByStatus() {
        try {
            List<Map<String, Object>> statusStats = analyticsService.getApplicationsByStatus();
            return ResponseEntity.ok(ApiResponse.success("Application status statistics retrieved successfully", statusStats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve application statistics", e.getMessage()));
        }
    }

    @GetMapping("/jobs-by-status")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getJobsByStatus() {
        try {
            List<Map<String, Object>> statusStats = analyticsService.getJobsByStatus();
            return ResponseEntity.ok(ApiResponse.success("Job status statistics retrieved successfully", statusStats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve job statistics", e.getMessage()));
        }
    }

    @GetMapping("/applications-per-job")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getApplicationsPerJob() {
        try {
            List<Map<String, Object>> jobStats = analyticsService.getApplicationsPerJob();
            return ResponseEntity.ok(ApiResponse.success("Applications per job statistics retrieved successfully", jobStats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve applications per job statistics", e.getMessage()));
        }
    }

    @GetMapping("/conversion-rates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConversionRates() {
        try {
            Map<String, Object> rates = analyticsService.getConversionRates();
            return ResponseEntity.ok(ApiResponse.success("Conversion rates retrieved successfully", rates));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve conversion rates", e.getMessage()));
        }
    }

    @GetMapping("/average-time-to-fill")
    public ResponseEntity<ApiResponse<Double>> getAverageTimeToFill() {
        try {
            double avgTime = analyticsService.getAverageTimeToFill();
            return ResponseEntity.ok(ApiResponse.success("Average time to fill retrieved successfully", avgTime));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve average time to fill", e.getMessage()));
        }
    }

    @GetMapping("/recruiter-performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRecruiterPerformance() {
        try {
            List<Map<String, Object>> performance = analyticsService.getRecruiterPerformance();
            return ResponseEntity.ok(ApiResponse.success("Recruiter performance retrieved successfully", performance));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve recruiter performance", e.getMessage()));
        }
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMonthlyTrends() {
        try {
            Map<String, Object> trends = analyticsService.getMonthlyTrends();
            return ResponseEntity.ok(ApiResponse.success("Monthly trends retrieved successfully", trends));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve monthly trends", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardSummary() {
        try {
            Map<String, Object> summary = Map.of(
                    "stats", analyticsService.getDashboardStats(),
                    "recentActivity", analyticsService.getRecentActivity(5),
                    "followUps", analyticsService.getFollowUpNeeded(),
                    "conversionRates", analyticsService.getConversionRates(),
                    "averageTimeToFill", analyticsService.getAverageTimeToFill()
            );
            return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", summary));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to retrieve dashboard summary", e.getMessage()));
        }
    }
}