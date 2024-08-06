package com.example.spring.repository;

import com.example.spring.document.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course,Long> {
    Page<Course> findAll(Pageable pageable);
}