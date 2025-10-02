package com.ats.service;

import com.ats.entity.Candidate;
import com.ats.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Page<Candidate> getAllCandidates(Pageable pageable) {
        return candidateRepository.findAll(pageable);
    }

    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }

    public Candidate createCandidate(Candidate candidate) {
        candidate.setCreatedAt(LocalDateTime.now());
        candidate.setUpdatedAt(LocalDateTime.now());
        return candidateRepository.save(candidate);
    }

    public Candidate updateCandidate(Long id, Candidate candidateDetails) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));

        candidate.setFirstName(candidateDetails.getFirstName());
        candidate.setLastName(candidateDetails.getLastName());
        candidate.setEmail(candidateDetails.getEmail());
        candidate.setPhone(candidateDetails.getPhone());
        candidate.setLocation(candidateDetails.getLocation());
        candidate.setCurrentCompany(candidateDetails.getCurrentCompany());
        candidate.setCurrentJobTitle(candidateDetails.getCurrentJobTitle());
        candidate.setExperienceYears(candidateDetails.getExperienceYears());
        candidate.setSkills(candidateDetails.getSkills());
        candidate.setSummary(candidateDetails.getSummary());
        candidate.setLinkedinUrl(candidateDetails.getLinkedinUrl());
        candidate.setResumeUrl(candidateDetails.getResumeUrl());
        candidate.setUpdatedAt(LocalDateTime.now());

        return candidateRepository.save(candidate);
    }

    public void deleteCandidate(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + id));
        candidateRepository.delete(candidate);
    }

    public List<Candidate> searchCandidates(String query) {
        return candidateRepository.findBySearchTerm(query);
    }

    public List<Candidate> searchCandidatesByName(String name) {
        return candidateRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    public List<Candidate> searchCandidatesByEmail(String email) {
        return candidateRepository.findByEmailContainingIgnoreCase(email);
    }

    public List<Candidate> searchCandidatesBySkills(String skills) {
        return candidateRepository.findBySkillsContainingIgnoreCase(skills);
    }

    public List<Candidate> getCandidatesByLocation(String location) {
        return candidateRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Candidate> getCandidatesByExperience(Integer minYears, Integer maxYears) {
        return candidateRepository.findByExperienceYearsBetween(minYears, maxYears);
    }

    public List<Candidate> getCandidatesByCurrentCompany(String company) {
        return candidateRepository.findByCurrentCompanyContainingIgnoreCase(company);
    }

    public boolean existsByEmail(String email) {
        return candidateRepository.existsByEmailIgnoreCase(email);
    }

    public Optional<Candidate> getCandidateByEmail(String email) {
        return candidateRepository.findByEmailIgnoreCase(email);
    }

    public long getTotalCandidatesCount() {
        return candidateRepository.count();
    }

    public List<Candidate> getCandidatesWithLinkedIn() {
        return candidateRepository.findByLinkedinUrlIsNotNull();
    }

    public List<Candidate> getCandidatesWithResume() {
        return candidateRepository.findByResumeUrlIsNotNull();
    }
}