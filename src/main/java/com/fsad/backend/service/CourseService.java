package com.fsad.backend.service;

import com.fsad.backend.entity.Course;
import com.fsad.backend.entity.UserRole;
import com.fsad.backend.repository.CourseRepository;
import com.fsad.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    public Course create(Course course) {
        validateTeacherExists(course.getTeacher());
        return courseRepository.save(course);
    }

    public Course update(Long id, Course updated) {
        Course course = getById(id);
        validateTeacherExists(updated.getTeacher());
        course.setCode(updated.getCode());
        course.setName(updated.getName());
        course.setTerm(updated.getTerm());
        course.setTeacher(updated.getTeacher());
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        courseRepository.delete(getById(id));
    }

    private void validateTeacherExists(String teacherName) {
        String normalizedTeacherName = teacherName == null ? "" : teacherName.trim();
        if (normalizedTeacherName.isEmpty()) {
            throw new IllegalArgumentException("Teacher name is required");
        }

        boolean teacherExists = userRepository.existsByNameIgnoreCaseAndRole(normalizedTeacherName, UserRole.TEACHER);
        if (!teacherExists) {
            throw new IllegalArgumentException("Teacher is not available in users list");
        }
    }
}
