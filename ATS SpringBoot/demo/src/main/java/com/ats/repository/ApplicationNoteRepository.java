package com.ats.repository;

import com.ats.entity.ApplicationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {

    List<ApplicationNote> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);

    List<ApplicationNote> findByNoteType(ApplicationNote.NoteType noteType);

    @Query("SELECT n FROM ApplicationNote n WHERE n.scheduledFollowUp IS NOT NULL AND n.scheduledFollowUp <= :date")
    List<ApplicationNote> findNotesWithFollowUpDue(@Param("date") LocalDateTime date);

    @Query("SELECT n FROM ApplicationNote n WHERE n.createdBy.id = :userId ORDER BY n.createdAt DESC")
    List<ApplicationNote> findByCreatedByIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM ApplicationNote n WHERE n.application.id = :applicationId")
    Long countByApplicationId(@Param("applicationId") Long applicationId);

    List<ApplicationNote> findByScheduledFollowUpIsNotNullOrderByScheduledFollowUpAsc();

    List<ApplicationNote> findByScheduledFollowUpBeforeOrderByScheduledFollowUpAsc(LocalDateTime date);

    List<ApplicationNote> findTopByOrderByCreatedAtDesc();

    List<ApplicationNote> findByApplicationIdAndNoteTypeOrderByCreatedAtDesc(Long applicationId, ApplicationNote.NoteType noteType);

    List<ApplicationNote> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<ApplicationNote> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String searchTerm);

    long countByCreatedById(Long userId);

    long countByNoteType(ApplicationNote.NoteType noteType);

    List<ApplicationNote> findByScheduledFollowUpBetweenOrderByScheduledFollowUpAsc(LocalDateTime startDate, LocalDateTime endDate);
}