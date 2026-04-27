package com.fsad.backend.service;

import com.fsad.backend.entity.Assignment;
import com.fsad.backend.entity.Course;
import com.fsad.backend.repository.AssignmentRepository;
import com.fsad.backend.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final Path uploadRoot;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository, @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAllByOrderByDeadlineDesc();
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with id: " + id));
    }

    public Assignment createAssignment(Long courseId, String title, String description, LocalDateTime deadline, Integer maxMarks, String uploadedBy) {
        return createAssignment(courseId, title, description, deadline, maxMarks, uploadedBy, "");
    }

    public Assignment createAssignment(Long courseId, String title, String description, LocalDateTime deadline, Integer maxMarks, String uploadedBy, String filePath) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        Assignment assignment = new Assignment();
        assignment.setCourseId(course.getId());
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDeadline(deadline);
        assignment.setMaxMarks(maxMarks);
        assignment.setUploadedBy(uploadedBy);
        assignment.setFilePath(filePath == null ? "" : filePath);

        return assignmentRepository.save(assignment);
    }

    public Assignment createAssignment(String title, String description, LocalDateTime deadline, String uploadedBy, MultipartFile file) {
        String storedFilePath = saveAssignmentFile(file);

        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDeadline(deadline);
        assignment.setMaxMarks(20);
        assignment.setUploadedBy(uploadedBy);
        assignment.setFilePath(storedFilePath);

        return assignmentRepository.save(assignment);
    }

    // Stores an uploaded assignment file on local disk and returns the full saved path.
    public String saveAssignmentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is required");
        }

        try {
            Files.createDirectories(uploadRoot);
            String originalName = file.getOriginalFilename() == null ? "assignment.bin" : file.getOriginalFilename();
            String sanitizedName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String storedName = UUID.randomUUID() + "-" + sanitizedName;
            Path target = uploadRoot.resolve(storedName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return target.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded file", ex);
        }
    }
}
