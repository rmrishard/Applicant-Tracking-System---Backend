package com.ats.service;

import com.ats.entity.ApplicationNote;
import com.ats.entity.Application;
import com.ats.entity.User;
import com.ats.repository.ApplicationNoteRepository;
import com.ats.repository.ApplicationRepository;
import com.ats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicationNoteService {

    @Autowired
    private ApplicationNoteRepository applicationNoteRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ApplicationNote> getAllNotes() {
        return applicationNoteRepository.findAll();
    }

    public Page<ApplicationNote> getAllNotes(Pageable pageable) {
        return applicationNoteRepository.findAll(pageable);
    }

    public Optional<ApplicationNote> getNoteById(Long id) {
        return applicationNoteRepository.findById(id);
    }

    public ApplicationNote createNote(ApplicationNote note) {
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        return applicationNoteRepository.save(note);
    }

    public ApplicationNote createNote(Long applicationId, Long createdById, String content,
                                     ApplicationNote.NoteType noteType, LocalDateTime scheduledFollowUp) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + applicationId));

        User createdBy = userRepository.findById(createdById)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + createdById));

        ApplicationNote note = new ApplicationNote();
        note.setApplication(application);
        note.setCreatedBy(createdBy);
        note.setContent(content);
        note.setNoteType(noteType);
        note.setScheduledFollowUp(scheduledFollowUp);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        return applicationNoteRepository.save(note);
    }

    public ApplicationNote updateNote(Long id, ApplicationNote noteDetails) {
        ApplicationNote note = applicationNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        note.setContent(noteDetails.getContent());
        note.setNoteType(noteDetails.getNoteType());
        note.setScheduledFollowUp(noteDetails.getScheduledFollowUp());
        note.setUpdatedAt(LocalDateTime.now());

        return applicationNoteRepository.save(note);
    }

    public void deleteNote(Long id) {
        ApplicationNote note = applicationNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        applicationNoteRepository.delete(note);
    }

    public List<ApplicationNote> getNotesByApplication(Long applicationId) {
        return applicationNoteRepository.findByApplicationIdOrderByCreatedAtDesc(applicationId);
    }

    public List<ApplicationNote> getNotesByCreatedBy(Long userId) {
        return applicationNoteRepository.findByCreatedByIdOrderByCreatedAtDesc(userId);
    }

    public List<ApplicationNote> getNotesByNoteType(ApplicationNote.NoteType noteType) {
        return applicationNoteRepository.findByNoteType(noteType);
    }

    public List<ApplicationNote> getNotesWithScheduledFollowUp() {
        return applicationNoteRepository.findByScheduledFollowUpIsNotNullOrderByScheduledFollowUpAsc();
    }

    public List<ApplicationNote> getNotesWithUpcomingFollowUp(LocalDateTime beforeDate) {
        return applicationNoteRepository.findByScheduledFollowUpBeforeOrderByScheduledFollowUpAsc(beforeDate);
    }

    public List<ApplicationNote> getNotesWithOverdueFollowUp() {
        return applicationNoteRepository.findByScheduledFollowUpBeforeOrderByScheduledFollowUpAsc(LocalDateTime.now());
    }

    public List<ApplicationNote> getRecentNotes(int limit) {
        return applicationNoteRepository.findTopByOrderByCreatedAtDesc();
    }

    public List<ApplicationNote> getNotesByApplicationAndType(Long applicationId, ApplicationNote.NoteType noteType) {
        return applicationNoteRepository.findByApplicationIdAndNoteTypeOrderByCreatedAtDesc(applicationId, noteType);
    }

    public List<ApplicationNote> getNotesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return applicationNoteRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    public List<ApplicationNote> searchNotesContent(String searchTerm) {
        return applicationNoteRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(searchTerm);
    }

    public long getTotalNotesCount() {
        return applicationNoteRepository.count();
    }

    public long getNotesCountByApplication(Long applicationId) {
        return applicationNoteRepository.countByApplicationId(applicationId);
    }

    public long getNotesCountByCreatedBy(Long userId) {
        return applicationNoteRepository.countByCreatedById(userId);
    }

    public long getNotesCountByNoteType(ApplicationNote.NoteType noteType) {
        return applicationNoteRepository.countByNoteType(noteType);
    }

    public List<ApplicationNote> getNotesToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return applicationNoteRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startOfDay, endOfDay);
    }

    public List<ApplicationNote> getFollowUpNotesForToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return applicationNoteRepository.findByScheduledFollowUpBetweenOrderByScheduledFollowUpAsc(startOfDay, endOfDay);
    }

    public ApplicationNote addQuickNote(Long applicationId, Long createdById, String content) {
        return createNote(applicationId, createdById, content, ApplicationNote.NoteType.GENERAL, null);
    }

    public ApplicationNote addCallNote(Long applicationId, Long createdById, String content, LocalDateTime followUp) {
        return createNote(applicationId, createdById, content, ApplicationNote.NoteType.CALL, followUp);
    }

    public ApplicationNote addEmailNote(Long applicationId, Long createdById, String content) {
        return createNote(applicationId, createdById, content, ApplicationNote.NoteType.EMAIL, null);
    }

    public ApplicationNote addInterviewNote(Long applicationId, Long createdById, String content, LocalDateTime followUp) {
        return createNote(applicationId, createdById, content, ApplicationNote.NoteType.INTERVIEW, followUp);
    }
}