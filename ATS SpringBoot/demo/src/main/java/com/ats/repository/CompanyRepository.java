package com.ats.repository;

import com.ats.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.industry) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Company> findBySearchTerm(@Param("searchTerm") String searchTerm);

    List<Company> findByIndustryIgnoreCase(String industry);

    List<Company> findByLocationIgnoreCase(String location);

    List<Company> findByNameContainingIgnoreCase(String name);

    List<Company> findByIndustryContainingIgnoreCase(String industry);

    List<Company> findByLocationContainingIgnoreCase(String location);

    List<Company> findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCase(String name, String industry);

    boolean existsByNameIgnoreCase(String name);
}