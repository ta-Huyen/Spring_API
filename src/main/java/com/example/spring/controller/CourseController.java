package com.example.spring.controller;

import com.example.spring.configuration.Constant;
import com.example.spring.document.Course;
import com.example.spring.exception.ResourceNotFoundException;
import com.example.spring.service.CourseService;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public ModelAndView showCourses(Model model) {
        return findPaginated(1, model);
    }

    @GetMapping("/update/{id}")
    public ModelAndView showUpdateCourse(@PathVariable("id") Long id, Model model) {
        Course course = courseService.findCourseById(id);
        if (course == null) {
            model.addAttribute("idNotFound", id);
            return showCourses(model);
        }
        model.addAttribute("updateCourse", course);

        return new ModelAndView("update-course");
    }

    @GetMapping("/page/{pageNo}")
    public ModelAndView findPaginated(@PathVariable(value="pageNo") int pageNo, Model model) {
        int size = Constant.DEFAULT_PAGE_SIZE;

        Page<Course> page = courseService.findPaginated(pageNo, size);
        List<Course> listCourses = page.getContent();

        model.addAttribute("course", new Course());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listCourses", listCourses);

        return new ModelAndView("course-list");
    }

    @PostMapping("/add")
    public ModelAndView addCourse(@Valid Course request, BindingResult bindingResult, Model model) {
        model.addAttribute("isAdd", true);

        if (courseService.existsByName(request.getName())) {
            bindingResult.rejectValue("name", "error.name", "Course name is already exist!");
            log.error("Error: ", new BadRequestException("Course name is already exist"));
        }
        if (bindingResult.hasErrors()){
            return new ModelAndView("course-list");
        }

        courseService.addCourse(request);

        return showCourses(model);
    }

    @GetMapping("/find")
    public ModelAndView findCourse(@RequestParam("byId") String strId, @RequestParam("byName") String name, Model model) {
        List<Course> listCourses = new ArrayList<>();

        if (Constant.isNumeric(strId)) {
            long id = Long.parseLong(strId);
            listCourses.addAll(courseService.findCourse(id, name));
        } else {
            listCourses.addAll(courseService.findCourse(-1, name));
            if (!strId.isBlank()) {
                model.addAttribute("idError", true);
            }
        }

        model.addAttribute("course", new Course());
        model.addAttribute("byId", strId);
        model.addAttribute("byName", name);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 0);
        model.addAttribute("totalItems", listCourses.size());
        model.addAttribute("listCourses", listCourses);

        return new ModelAndView("course-list");
    }

    @PostMapping("/update/{id}")
    public ModelAndView updateCourse(@PathVariable("id") long id, @ModelAttribute("updateCourse") Course request,
                                     Model model, BindingResult bindingResult) {
        Optional<Course> courseByName = courseService.findCourseByName(request.getName());
        if (courseByName.isPresent() && (courseByName.get().getId() != id)) {
            bindingResult.rejectValue("name", "error.name", "Course name is already exist!");
            log.error("Error: ", new BadRequestException("Course name is already exist"));
        }
        if (bindingResult.hasErrors()){
            return new ModelAndView("update-course");
        }
        courseService.updateCourse(id, request);

        return showCourses(model);
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteCourse(@PathVariable("id") long id, Model model) {
        courseService.deleteCourse(id);
        return showCourses(model);
    }
}