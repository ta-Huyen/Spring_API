package com.example.spring.repository;

import com.example.spring.document.Course;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course,Long> {
}