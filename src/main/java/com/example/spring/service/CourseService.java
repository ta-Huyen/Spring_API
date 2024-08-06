package com.example.spring.service;

import com.example.spring.document.Course;
import com.example.spring.exception.ResourceNotFoundException;
import com.example.spring.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;

    public void createCourse(Course course) {
        courseRepo.save(course);
    }

    public Optional<Course> findCourseById(long id) {
        return courseRepo.findById(id);
    }

    public Page<Course> findPaginated(int pageNo, int size) {
        return courseRepo.findAll(PageRequest.of(pageNo-1, size));
    }

    public Course saveCourse(Course course) {
        return courseRepo.save(course);
    }

    public Course updateCourse(Course course, long id) throws ResourceNotFoundException {
        Optional<Course> courseById = findCourseById(id);
        if (courseById.isPresent()) {
            Course newCourse = courseById.get();

            newCourse.setName(course.getName());
            newCourse.setDescription(course.getDescription());

            return courseRepo.save(newCourse);
        }

        throw new ResourceNotFoundException("Course not found");
    }

    public void deleteCourseById(long id) {
        courseRepo.deleteById(id);
    }
}