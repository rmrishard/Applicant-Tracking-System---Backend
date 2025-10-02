package com.ats.service;

import com.ats.entity.Company;
import com.ats.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Page<Company> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Company createCompany(Company company) {
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        return companyRepository.save(company);
    }

    public Company updateCompany(Long id, Company companyDetails) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));

        company.setName(companyDetails.getName());
        company.setDescription(companyDetails.getDescription());
        company.setIndustry(companyDetails.getIndustry());
        company.setLocation(companyDetails.getLocation());
        company.setWebsite(companyDetails.getWebsite());
        company.setPhone(companyDetails.getPhone());
        company.setUpdatedAt(LocalDateTime.now());

        return companyRepository.save(company);
    }

    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        companyRepository.delete(company);
    }

    public List<Company> searchCompaniesByName(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Company> searchCompaniesByIndustry(String industry) {
        return companyRepository.findByIndustryContainingIgnoreCase(industry);
    }

    public List<Company> searchCompanies(String query) {
        return companyRepository.findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCase(query, query);
    }

    public boolean existsByName(String name) {
        return companyRepository.existsByNameIgnoreCase(name);
    }

    public long getTotalCompaniesCount() {
        return companyRepository.count();
    }

    public List<Company> getCompaniesByLocation(String location) {
        return companyRepository.findByLocationContainingIgnoreCase(location);
    }
}