package com.example.spring.repository;

import com.example.spring.document.Course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course,Long> {
    Page<Course> findAll(Pageable pageable);

    @Query("{'name' : ?0 }")
    Optional<Course> findByName(@Param("name") String name);

    List<Course> findByNameStartingWith(String name);
}