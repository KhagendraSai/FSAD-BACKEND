package com.fsad.backend.controller;

import com.fsad.backend.dto.GradeSubmissionRequest;
import com.fsad.backend.dto.LinkSubmissionRequest;
import com.fsad.backend.entity.Submission;
import com.fsad.backend.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Submissions", description = "Student submission and teacher grading endpoints")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(value = "/assignment/{assignmentId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a student submission file for an assignment")
    public ResponseEntity<Submission> uploadSubmission(
            @PathVariable Long assignmentId,
            @RequestParam String submittedBy,
            @RequestParam("file") MultipartFile file) {

        Submission created = submissionService.uploadSubmission(assignmentId, submittedBy, file);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping(value = "/assignment/{assignmentId}/link")
    @Operation(summary = "Submit a link for an assignment")
    public ResponseEntity<Submission> submitLink(
            @PathVariable Long assignmentId,
            @RequestParam String submittedBy,
            @RequestBody LinkSubmissionRequest request) {

        Submission created = submissionService.submitLink(assignmentId, submittedBy, request.getSubmissionLink());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{submissionId}/grade")
    @Operation(summary = "Save grade and remarks for a submission")
    public ResponseEntity<Submission> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody GradeSubmissionRequest request) {

        Submission updated = submissionService.gradeSubmission(submissionId, request.getGrade(), request.getFeedback());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{submissionId}/file")
    @Operation(summary = "Open a submitted file directly in browser")
    public ResponseEntity<Resource> openSubmissionFile(@PathVariable Long submissionId) {
        Submission submission = submissionService.getSubmissionById(submissionId);

        if (isRemoteUrl(submission.getSubmissionPath())) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(submission.getSubmissionPath()))
                    .build();
        }

        Resource resource = submissionService.getSubmissionResource(submissionId);
        String contentType = submissionService.getSubmissionContentType(submissionId);

        ContentDisposition disposition = ContentDisposition.inline()
                .filename(submission.getOriginalFileName() == null || submission.getOriginalFileName().isBlank() ? "submission" : submission.getOriginalFileName())
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(resource);
    }

    private boolean isRemoteUrl(String value) {
        return value != null && (value.startsWith("http://") || value.startsWith("https://"));
    }
}
