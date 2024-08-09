package com.example.spring.service;

import com.example.spring.document.Course;
import com.example.spring.exception.ResourceNotFoundException;
import com.example.spring.repository.CourseRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private SequenceGeneratorService sequenceService;
    private RedisTemplate<String, Course> redisTemplate;
    private RabbitTemplate rabbitTemplate;

    public Boolean existsByName(String name) {
        return courseRepo.findByName(name).isPresent();
    }

    public Optional<Course> findCourseByName(String name) {
        return courseRepo.findByName(name);
    }

    public Course findCourseById(long id) {
        String key = "course_" + id;
        ValueOperations<String, Course> operations = redisTemplate.opsForValue();
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (hasKey) {
            Course course = operations.get(key);
            log.info("CourseService.findCourseById() : cache course >> " + course.toString());
            return course;
        }
        Optional<Course> course = courseRepo.findById(id);
        if (course.isPresent()) {
            operations.set(key, course.get(), 10, TimeUnit.SECONDS);
            log.info("CourseService.findCourseById() : cache insert >> " + course.get().toString());
            return course.get();
        }

        log.error("Error: ", new ResourceNotFoundException("Course " + id + " not found"));
        return null;
    }

    public List<Course> findCourse(long id, String name) {
        Course courseById = findCourseById(id);
        List<Course> courseByName = (name.isBlank()) ? new ArrayList<>() : courseRepo.findByNameStartingWith(name);
        if (courseById != null) {
            if (courseByName.isEmpty() || courseByName.contains(courseById)) {
                return List.of(courseById);
            } else {
                return new ArrayList<>();
            }
        }
        return courseByName;
    }

    public Page<Course> findPaginated(int pageNo, int size) {
        return courseRepo.findAll(PageRequest.of(pageNo-1, size));
    }

    public void addCourse(Course request) {
        Course course = new Course();
        course.setId(sequenceService.getSequenceNumber(Course.SEQUENCE_NAME));
        course.setName(request.getName());
        course.setDescription(request.getDescription());

        courseRepo.save(course);
        log.info("Course create successfully!");
    }

    public void updateCourse(long id, Course request){
        Course courseById = findCourseById(id);
        if (courseById != null) {
            courseById.setName(request.getName());
            courseById.setDescription(request.getDescription());

            String key = "course_" + id;
            boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
            if (hasKey) {
                redisTemplate.delete(key);
                log.info("CourseService.updateUser() : cache update >> " + courseById.toString());
            }
            courseRepo.save(courseById);
        } else {
            log.error("Error: ", new ResourceNotFoundException("Course with id " + id + " not found"));
        }
    }

    public void deleteCourse(long id) {
        String key = "course_" + id;
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (hasKey) {
            redisTemplate.delete(key);
            log.info("CourseService.deleteUser() : cache delete ID >> " + id);
        }

        Optional<Course> course = courseRepo.findById(id);
        if (course.isPresent()) {
            courseRepo.deleteById(id);
            log.info("Delete course with id" + id);
        } else {
            log.error("Error: ", new ResourceNotFoundException("Course with id " + id + " not found"));
        }
    }
}