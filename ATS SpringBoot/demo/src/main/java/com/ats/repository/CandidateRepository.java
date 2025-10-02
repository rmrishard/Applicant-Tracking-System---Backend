package com.ats.repository;

import com.ats.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Candidate c WHERE " +
           "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.skills) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.currentJobTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.currentCompany) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Candidate> findBySearchTerm(@Param("searchTerm") String searchTerm);

    @Query("SELECT c FROM Candidate c WHERE LOWER(c.skills) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Candidate> findBySkill(@Param("skill") String skill);

    List<Candidate> findByExperienceYearsGreaterThanEqual(Integer years);

    List<Candidate> findByLocationIgnoreCase(String location);

    List<Candidate> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

    List<Candidate> findByEmailContainingIgnoreCase(String email);

    List<Candidate> findBySkillsContainingIgnoreCase(String skills);

    List<Candidate> findByLocationContainingIgnoreCase(String location);

    List<Candidate> findByExperienceYearsBetween(Integer minYears, Integer maxYears);

    List<Candidate> findByCurrentCompanyContainingIgnoreCase(String company);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Candidate> findByEmailIgnoreCase(String email);

    List<Candidate> findByLinkedinUrlIsNotNull();

    List<Candidate> findByResumeUrlIsNotNull();

    @Query("SELECT c FROM Candidate c WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.skills) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.currentJobTitle) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.currentCompany) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:location IS NULL OR :location = '' OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minExperience IS NULL OR c.experienceYears >= :minExperience) AND " +
           "(:maxExperience IS NULL OR c.experienceYears <= :maxExperience) AND " +
           "(:skills IS NULL OR :skills = '' OR LOWER(c.skills) LIKE LOWER(CONCAT('%', :skills, '%')))")
    Page<Candidate> findByAdvancedSearch(@Param("query") String query,
                                       @Param("location") String location,
                                       @Param("minExperience") Integer minExperience,
                                       @Param("maxExperience") Integer maxExperience,
                                       @Param("skills") String skills,
                                       Pageable pageable);
}