package com.example.spring.service;

import com.example.spring.document.Course;
import com.example.spring.repository.CourseRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private CourseRepository courseRepo;

    public void createCourse(Course course) {
        courseRepo.save(course);
    }

    public List<Course> getCourse() {
        return (List<Course>) courseRepo.findAll();
    }

    public Optional<Course> findCourseById(long id) {
        return courseRepo.findById(id);
    }

    public void deleteCourseById(long id) {
        courseRepo.deleteById(id);
    }

    public Course updateCourse(Course user, long id) {
        Optional<Course> usr = findCourseById(id);
        Course newuser = usr.get();
        return courseRepo.save(newuser);
    }
}
