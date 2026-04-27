package com.fsad.backend.repository;

import com.fsad.backend.entity.Submission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByAssignmentIdAndSubmittedByIgnoreCase(Long assignmentId, String submittedBy);
}
