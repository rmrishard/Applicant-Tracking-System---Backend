package com.ats.repository;

import com.ats.entity.Application;
import com.ats.entity.Candidate;
import com.ats.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByCandidateAndJob(Candidate candidate, Job job);

    List<Application> findByJobId(Long jobId);

    List<Application> findByCandidateId(Long candidateId);

    List<Application> findByStatus(Application.ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.job.id = :jobId ORDER BY a.appliedAt DESC")
    List<Application> findByJobIdOrderByAppliedAtDesc(@Param("jobId") Long jobId);

    @Query("SELECT a FROM Application a WHERE a.followUpDate IS NOT NULL AND a.followUpDate <= :date")
    List<Application> findApplicationsNeedingFollowUp(@Param("date") LocalDateTime date);

    @Query("SELECT a FROM Application a WHERE a.job.assignedRecruiter.id = :recruiterId")
    List<Application> findByAssignedRecruiter(@Param("recruiterId") Long recruiterId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.id = :jobId")
    Long countByJobId(@Param("jobId") Long jobId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.id = :jobId AND a.status = :status")
    Long countByJobIdAndStatus(@Param("jobId") Long jobId, @Param("status") Application.ApplicationStatus status);

    boolean existsByCandidateAndJob(Candidate candidate, Job job);

    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    Optional<Application> findByCandidateIdAndJobId(Long candidateId, Long jobId);

    List<Application> findByJobIdAndStatus(Long jobId, Application.ApplicationStatus status);

    List<Application> findByCandidateIdAndStatus(Long candidateId, Application.ApplicationStatus status);

    List<Application> findByFollowUpDateBeforeAndStatusNot(LocalDateTime date, Application.ApplicationStatus status);

    List<Application> findTopByOrderByAppliedAtDesc();

    long countByStatus(Application.ApplicationStatus status);

    long countByCandidateId(Long candidateId);

    List<Application> findByRatingBetween(Integer minRating, Integer maxRating);

    List<Application> findByRatingGreaterThanEqual(Integer minRating);

    List<Application> findByFollowUpDateIsNullAndStatusIn(List<Application.ApplicationStatus> statuses);

    long countByStatusNot(Application.ApplicationStatus status);

    @Query("SELECT a FROM Application a ORDER BY a.createdAt DESC LIMIT :limit")
    List<Application> findTopByOrderByCreatedAtDesc(@Param("limit") int limit);

    @Query("SELECT j.title, j.company.name, COUNT(a) FROM Application a JOIN a.job j GROUP BY j.id, j.title, j.company.name ORDER BY COUNT(a) DESC")
    List<Object[]> getApplicationCountPerJob();

    @Query("SELECT u.firstName, u.lastName, COUNT(DISTINCT j), COUNT(a), " +
           "SUM(CASE WHEN a.status = 'HIRED' THEN 1 ELSE 0 END) " +
           "FROM Application a " +
           "JOIN a.job j " +
           "JOIN j.assignedRecruiter u " +
           "GROUP BY u.id, u.firstName, u.lastName")
    List<Object[]> getRecruiterPerformance();

    @Query("SELECT MONTH(a.createdAt), YEAR(a.createdAt), COUNT(a) " +
           "FROM Application a " +
           "WHERE a.createdAt >= :since " +
           "GROUP BY YEAR(a.createdAt), MONTH(a.createdAt) " +
           "ORDER BY YEAR(a.createdAt), MONTH(a.createdAt)")
    List<Object[]> getMonthlyApplicationTrends(@Param("since") LocalDateTime since);

    @Query("SELECT MONTH(a.updatedAt), YEAR(a.updatedAt), COUNT(a) " +
           "FROM Application a " +
           "WHERE a.updatedAt >= :since AND a.status = 'HIRED' " +
           "GROUP BY YEAR(a.updatedAt), MONTH(a.updatedAt) " +
           "ORDER BY YEAR(a.updatedAt), MONTH(a.updatedAt)")
    List<Object[]> getMonthlyHireTrends(@Param("since") LocalDateTime since);
}