package com.fsad.backend.service;

import com.fsad.backend.entity.Mark;
import com.fsad.backend.repository.MarkRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarkService {

    private final MarkRepository markRepository;

    public MarkService(MarkRepository markRepository) {
        this.markRepository = markRepository;
    }

    public List<Mark> getAll(Long courseId) {
        if (courseId != null) {
            return markRepository.findByCourseId(courseId);
        }
        return markRepository.findAll();
    }

    public Mark getById(Long id) {
        return markRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mark not found with id: " + id));
    }

    public Mark create(Mark mark) {
        return markRepository.save(mark);
    }

    public Mark update(Long id, Mark updated) {
        Mark mark = getById(id);
        mark.setCourseId(updated.getCourseId());
        mark.setStudentName(updated.getStudentName());
        mark.setAssignmentTitle(updated.getAssignmentTitle());
        mark.setScore(updated.getScore());
        mark.setMaxScore(updated.getMaxScore());
        mark.setFeedback(updated.getFeedback());
        return markRepository.save(mark);
    }

    public void delete(Long id) {
        markRepository.delete(getById(id));
    }
}
