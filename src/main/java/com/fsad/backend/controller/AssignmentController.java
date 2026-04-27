package com.fsad.backend.controller;

import com.fsad.backend.entity.Assignment;
import com.fsad.backend.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/assignments")
@Tag(name = "Assignments", description = "Teacher and student assignment endpoints")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    @Operation(summary = "Get all assignments")
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by id")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    @PostMapping
    @Operation(summary = "Create assignment")
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        Assignment created = assignmentService.createAssignment(
                assignment.getCourseId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDeadline(),
                assignment.getMaxMarks(),
                assignment.getUploadedBy());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an assignment file and create assignment metadata")
    public ResponseEntity<Assignment> uploadAssignment(
            @RequestParam Long courseId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadline,
            @RequestParam(defaultValue = "20") Integer maxMarks,
            @RequestParam(defaultValue = "teacher") String uploadedBy,
            @RequestParam("file") MultipartFile file) {

        String storedFilePath = assignmentService.saveAssignmentFile(file);
        Assignment created = assignmentService.createAssignment(courseId, title, description, deadline, maxMarks, uploadedBy, storedFilePath);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
