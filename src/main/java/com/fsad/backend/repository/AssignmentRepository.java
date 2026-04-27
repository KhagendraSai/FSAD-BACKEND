package com.fsad.backend.repository;

import com.fsad.backend.entity.Assignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

	@EntityGraph(attributePaths = "submissions")
	List<Assignment> findAllByOrderByDeadlineDesc();

	@EntityGraph(attributePaths = "submissions")
	Optional<Assignment> findById(Long id);
}
