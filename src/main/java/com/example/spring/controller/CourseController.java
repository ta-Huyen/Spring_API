package com.example.spring.controller;

import com.example.spring.configuration.Constant;
import com.example.spring.document.Course;
import com.example.spring.service.CourseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public ModelAndView viewCourse(Model model) {
        return findPaginated(1, model);
    }

    @GetMapping("/page/{pageNo}")
    public ModelAndView findPaginated(@PathVariable(value="pageNo") int pageNo, Model model) {
        int size = Constant.DEFAULT_PAGE_SIZE;

        Page<Course> page = courseService.findPaginated(pageNo, size);
        List<Course> listCourses = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listCourses", listCourses);

        return new ModelAndView("/course-list");
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> getCourseById(@PathVariable("id") long id) {
        System.out.println("Fetching course with id " + id);
        Optional<Course> optcourse = courseService.findCourseById(id);
        Course course = optcourse.get();
        if (course == null) {
            return new ResponseEntity<Course> (HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Course> (course, HttpStatus.OK);
    }

    @PostMapping(value = "/create", headers = "Accept=application/json")
    public ResponseEntity<Void> createCourse(@RequestBody Course course, UriComponentsBuilder ucBuilder) {
        System.out.println("Creating course " + course.getName());
        courseService.createCourse(course);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/course/{id}").buildAndExpand(course.getId()).toUri());
        return new ResponseEntity<Void> (headers, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}", headers = "Accept=application/json")
    public ResponseEntity<Course> deleteCourse(@PathVariable("id") long id) {
        Optional<Course> optcourse = courseService.findCourseById(id);
        Course course = optcourse.get();
        if (course == null) {
            return new ResponseEntity<Course> (HttpStatus.NOT_FOUND);
        }
        courseService.deleteCourseById(id);
        return new ResponseEntity<Course> (HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{id}", headers = "Accept=application/json")
    public ResponseEntity<Course> updateCourse(@PathVariable("id") long id,
                                               @RequestBody Course currentcourse) {
        Optional<Course> optcourse = courseService.findCourseById(id);
        Course course = optcourse.get();
        if (course == null) {
            return new ResponseEntity<Course> (HttpStatus.NOT_FOUND);
        }
        Course usr = courseService.updateCourse(currentcourse, id);
        return new ResponseEntity<Course> (usr, HttpStatus.OK);
    }
}