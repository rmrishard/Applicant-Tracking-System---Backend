package com.ats.repository;

import com.ats.entity.Job;
import com.ats.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByStatus(Job.JobStatus status);

    List<Job> findByPriority(Job.Priority priority);

    List<Job> findByCompanyId(Long companyId);

    List<Job> findByAssignedRecruiter(User assignedRecruiter);

    List<Job> findByAssignedRecruiterId(Long recruiterId);

    List<Job> findByJobType(Job.JobType jobType);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByDeadlineBetween(LocalDate startDate, LocalDate endDate);

    List<Job> findByDeadlineBefore(LocalDate date);

    List<Job> findByStatusAndPriority(Job.JobStatus status, Job.Priority priority);

    List<Job> findByStatusAndCompanyId(Job.JobStatus status, Long companyId);

    long countByStatus(Job.JobStatus status);

    long countByCompanyId(Long companyId);

    long countByAssignedRecruiterId(Long recruiterId);

    List<Job> findTopByOrderByCreatedAtDesc();

    @Query("SELECT j FROM Job j WHERE j.status = :status ORDER BY j.priority DESC, j.createdAt DESC")
    List<Job> findByStatusOrderByPriorityAndCreatedAt(@Param("status") Job.JobStatus status);

    @Query("SELECT j FROM Job j WHERE " +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Job> findBySearchTerm(@Param("searchTerm") String searchTerm);

    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.status = :status")
    List<Job> findByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") Job.JobStatus status);

    @Query("SELECT j FROM Job j WHERE " +
           "(j.minSalary IS NULL OR j.minSalary <= :maxSalary) AND " +
           "(j.maxSalary IS NULL OR j.maxSalary >= :minSalary)")
    List<Job> findBySalaryRange(@Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary);

    @Query("SELECT j FROM Job j WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(j.company.name) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:location IS NULL OR :location = '' OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:status IS NULL OR j.status = :status) AND " +
           "(:minSalary IS NULL OR j.maxSalary IS NULL OR j.maxSalary >= :minSalary) AND " +
           "(:maxSalary IS NULL OR j.minSalary IS NULL OR j.minSalary <= :maxSalary) AND " +
           "(:companyId IS NULL OR j.company.id = :companyId)")
    Page<Job> findByAdvancedSearch(@Param("query") String query,
                                 @Param("location") String location,
                                 @Param("jobType") Job.JobType jobType,
                                 @Param("status") Job.JobStatus status,
                                 @Param("minSalary") BigDecimal minSalary,
                                 @Param("maxSalary") BigDecimal maxSalary,
                                 @Param("companyId") Long companyId,
                                 Pageable pageable);
}