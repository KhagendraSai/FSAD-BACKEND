package com.fsad.backend.repository;

import com.fsad.backend.entity.Mark;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByCourseId(Long courseId);
}
