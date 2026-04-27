package com.fsad.backend.service;

import com.fsad.backend.entity.Assignment;
import com.fsad.backend.entity.Submission;
import com.fsad.backend.repository.AssignmentRepository;
import com.fsad.backend.repository.SubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final FileStorageService fileStorageService;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            FileStorageService fileStorageService) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.fileStorageService = fileStorageService;
    }

    public Submission uploadSubmission(Long assignmentId, String submittedBy, MultipartFile file) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + assignmentId));

        String studentName = submittedBy == null ? "Student" : submittedBy.trim();
        if (studentName.isBlank()) {
            throw new IllegalArgumentException("submittedBy is required");
        }

        String storedPath = fileStorageService.store(file, "submissions");
        Submission submission = submissionRepository
                .findByAssignmentIdAndSubmittedByIgnoreCase(assignmentId, studentName)
                .orElseGet(Submission::new);

        submission.setAssignment(assignment);
        submission.setSubmittedBy(studentName);
        submission.setSubmissionPath(storedPath);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setOriginalFileName(file.getOriginalFilename() == null ? "submission" : file.getOriginalFilename());
        submission.setMimeType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        submission.setGrade(null);
        submission.setFeedback("");

        return submissionRepository.save(submission);
    }

    public Submission submitLink(Long assignmentId, String submittedBy, String submissionLink) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + assignmentId));

        String studentName = submittedBy == null ? "Student" : submittedBy.trim();
        if (studentName.isBlank()) {
            throw new IllegalArgumentException("submittedBy is required");
        }

        if (submissionLink == null || submissionLink.trim().isBlank()) {
            throw new IllegalArgumentException("Submission link is required");
        }

        Submission submission = submissionRepository
                .findByAssignmentIdAndSubmittedByIgnoreCase(assignmentId, studentName)
                .orElseGet(Submission::new);

        submission.setAssignment(assignment);
        submission.setSubmittedBy(studentName);
        submission.setSubmissionPath(submissionLink.trim());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setOriginalFileName(null);
        submission.setMimeType(null);
        submission.setGrade(null);
        submission.setFeedback("");

        return submissionRepository.save(submission);
    }

    public Submission gradeSubmission(Long submissionId, Double grade, String feedback) {
        Submission submission = getSubmissionById(submissionId);
        submission.setGrade(grade);
        submission.setFeedback(feedback == null ? "" : feedback.trim());
        return submissionRepository.save(submission);
    }

    public Submission getSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found with id: " + submissionId));
    }

    public Resource getSubmissionResource(Long submissionId) {
        Submission submission = getSubmissionById(submissionId);
        return fileStorageService.asResource(submission.getSubmissionPath());
    }

    public String getSubmissionContentType(Long submissionId) {
        Submission submission = getSubmissionById(submissionId);
        return fileStorageService.detectContentType(submission.getSubmissionPath(), submission.getMimeType());
    }
}
