package com.ats.controller;

import com.ats.entity.ApplicationNote;
import com.ats.service.ApplicationNoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/application-notes")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
public class ApplicationNoteController {

    @Autowired
    private ApplicationNoteService applicationNoteService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ApplicationNote> notePage = applicationNoteService.getAllNotes(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("notes", notePage.getContent());
            response.put("currentPage", notePage.getNumber());
            response.put("totalItems", notePage.getTotalElements());
            response.put("totalPages", notePage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            Optional<ApplicationNote> note = applicationNoteService.getNoteById(id);
            if (note.isPresent()) {
                return ResponseEntity.ok(note.get());
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Note not found with id: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> createNote(@Valid @RequestBody ApplicationNote note) {
        try {
            ApplicationNote savedNote = applicationNoteService.createNote(note);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/quick")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> addQuickNote(
            @RequestParam Long applicationId,
            @RequestParam Long createdById,
            @RequestParam String content) {
        try {
            ApplicationNote savedNote = applicationNoteService.addQuickNote(applicationId, createdById, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create quick note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/call")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> addCallNote(
            @RequestParam Long applicationId,
            @RequestParam Long createdById,
            @RequestParam String content,
            @RequestParam(required = false) String followUpDate) {
        try {
            LocalDateTime followUp = followUpDate != null ? LocalDateTime.parse(followUpDate) : null;
            ApplicationNote savedNote = applicationNoteService.addCallNote(applicationId, createdById, content, followUp);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create call note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> addEmailNote(
            @RequestParam Long applicationId,
            @RequestParam Long createdById,
            @RequestParam String content) {
        try {
            ApplicationNote savedNote = applicationNoteService.addEmailNote(applicationId, createdById, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create email note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/interview")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> addInterviewNote(
            @RequestParam Long applicationId,
            @RequestParam Long createdById,
            @RequestParam String content,
            @RequestParam(required = false) String followUpDate) {
        try {
            LocalDateTime followUp = followUpDate != null ? LocalDateTime.parse(followUpDate) : null;
            ApplicationNote savedNote = applicationNoteService.addInterviewNote(applicationId, createdById, content, followUp);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create interview note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @Valid @RequestBody ApplicationNote noteDetails) {
        try {
            ApplicationNote updatedNote = applicationNoteService.updateNote(id, noteDetails);
            return ResponseEntity.ok(updatedNote);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        try {
            applicationNoteService.deleteNote(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Note deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete note: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<?> getNotesByApplication(@PathVariable Long applicationId) {
        try {
            List<ApplicationNote> notes = applicationNoteService.getNotesByApplication(applicationId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch notes by application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotesByCreatedBy(@PathVariable Long userId) {
        try {
            List<ApplicationNote> notes = applicationNoteService.getNotesByCreatedBy(userId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch notes by user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/by-type")
    public ResponseEntity<?> getNotesByType(@RequestParam ApplicationNote.NoteType noteType) {
        try {
            List<ApplicationNote> notes = applicationNoteService.getNotesByNoteType(noteType);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch notes by type: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/with-follow-up")
    public ResponseEntity<?> getNotesWithScheduledFollowUp() {
        try {
            List<ApplicationNote> notes = applicationNoteService.getNotesWithScheduledFollowUp();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch notes with follow-up: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/follow-up-due")
    public ResponseEntity<?> getNotesWithUpcomingFollowUp(@RequestParam(required = false) String beforeDate) {
        try {
            LocalDateTime date = beforeDate != null ? LocalDateTime.parse(beforeDate) : LocalDateTime.now().plusDays(7);
            List<ApplicationNote> notes = applicationNoteService.getNotesWithUpcomingFollowUp(date);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch notes with upcoming follow-up: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/overdue-follow-up")
    public ResponseEntity<?> getNotesWithOverdueFollowUp() {
        try {
            List<ApplicationNote> notes = applicationNoteService.getNotesWithOverdueFollowUp();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch notes with overdue follow-up: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentNotes() {
        try {
            List<ApplicationNote> notes = applicationNoteService.getRecentNotes(20);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch recent notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getNotesToday() {
        try {
            List<ApplicationNote> notes = applicationNoteService.getNotesToday();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch today's notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/follow-up-today")
    public ResponseEntity<?> getFollowUpNotesForToday() {
        try {
            List<ApplicationNote> notes = applicationNoteService.getFollowUpNotesForToday();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch today's follow-up notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchNotesContent(@RequestParam String q) {
        try {
            List<ApplicationNote> notes = applicationNoteService.searchNotesContent(q);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to search notes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/timeline/application/{applicationId}")
    public ResponseEntity<?> getApplicationTimeline(@PathVariable Long applicationId) {
        try {
            List<ApplicationNote> notes = applicationNoteService.getNotesByApplication(applicationId);
            Map<String, Object> timeline = new HashMap<>();
            timeline.put("applicationId", applicationId);
            timeline.put("notes", notes);
            timeline.put("totalNotes", notes.size());
            return ResponseEntity.ok(timeline);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch application timeline: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getTotalNotesCount() {
        try {
            long count = applicationNoteService.getTotalNotesCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get notes count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/count/application/{applicationId}")
    public ResponseEntity<?> getNotesCountByApplication(@PathVariable Long applicationId) {
        try {
            long count = applicationNoteService.getNotesCountByApplication(applicationId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            response.put("applicationId", applicationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get notes count by application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}