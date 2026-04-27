package com.fsad.backend.config;

import com.fsad.backend.entity.Assignment;
import com.fsad.backend.entity.Course;
import com.fsad.backend.entity.Submission;
import com.fsad.backend.repository.AssignmentRepository;
import com.fsad.backend.repository.CourseRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AssignmentSeeder {

    @Bean
    CommandLineRunner seedAssignments(
            AssignmentRepository assignmentRepository,
            CourseRepository courseRepository,
            @Value("${app.seed.assignments:false}") boolean seedAssignmentsEnabled) {
        return args -> {
            if (!seedAssignmentsEnabled) {
                return;
            }

            if (assignmentRepository.count() > 0) {
                return;
            }

            List<Course> courses = courseRepository.findAll();
            if (courses.isEmpty()) {
                return;
            }

            seedAssignment(
                    assignmentRepository,
                    courses.get(0),
                    "Variables and Conditionals",
                    "Practice basic input, output, and branching.",
                    LocalDateTime.of(2026, 4, 11, 23, 59),
                    20,
                    "Taylor Teacher",
                    List.of(
                            buildSubmission("Sam Student", "https://mozilla.github.io/pdf.js/web/compressed.tracemonkey-pldi-09.pdf", LocalDateTime.of(2026, 4, 4, 15, 30), 18.0, "Clear structure and correct control flow."),
                            buildSubmission("Mina Patel", "https://mozilla.github.io/pdf.js/web/compressed.tracemonkey-pldi-09.pdf", LocalDateTime.of(2026, 4, 4, 17, 25), null, "")
                    )
            );

            if (courses.size() > 1) {
                seedAssignment(
                        assignmentRepository,
                        courses.get(1),
                        "Linked List Implementation",
                        "Implement insert, delete, search, and traversal.",
                        LocalDateTime.of(2026, 4, 16, 23, 59),
                        30,
                        "Taylor Teacher",
                        List.of()
                );
            }

            if (courses.size() > 2) {
                seedAssignment(
                        assignmentRepository,
                        courses.get(2),
                        "Responsive Dashboard Sprint",
                        "Build a responsive dashboard using modern layout rules.",
                        LocalDateTime.of(2026, 4, 21, 23, 59),
                        40,
                        "Taylor Teacher",
                        List.of()
                );
            }
        };
    }

    private void seedAssignment(
            AssignmentRepository assignmentRepository,
            Course course,
            String title,
            String description,
            LocalDateTime deadline,
            Integer maxMarks,
            String uploadedBy,
            List<SubmissionTemplate> submissionTemplates) {
        Assignment assignment = new Assignment();
        assignment.setCourseId(course.getId());
        assignment.setTitle(title);
        assignment.setDescription(description);
        assignment.setDeadline(deadline);
        assignment.setMaxMarks(maxMarks);
        assignment.setUploadedBy(uploadedBy);
        assignment.setFilePath("");

        List<Submission> submissions = new ArrayList<>();
        for (SubmissionTemplate template : submissionTemplates) {
            Submission submission = new Submission();
            submission.setAssignment(assignment);
            submission.setSubmittedBy(template.studentName());
            submission.setSubmissionPath(template.fileUrl());
            submission.setSubmittedAt(template.submittedAt());
            submission.setGrade(template.grade());
            submission.setFeedback(template.feedback());
            submissions.add(submission);
        }
        assignment.setSubmissions(submissions);
        assignmentRepository.save(assignment);
    }

    private SubmissionTemplate buildSubmission(String studentName, String fileUrl, LocalDateTime submittedAt, Double grade, String feedback) {
        return new SubmissionTemplate(studentName, fileUrl, submittedAt, grade, feedback);
    }

    private record SubmissionTemplate(String studentName, String fileUrl, LocalDateTime submittedAt, Double grade, String feedback) {
    }
}
