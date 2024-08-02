package com.example.spring.controller;

import com.example.spring.document.Course;
import com.example.spring.entity.User;
import com.example.spring.repository.CourseRepository;
import com.example.spring.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CourseRepository courseRepo;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/home/return")
    public String homePage() {
        return "home-page";
    }

    @PostMapping("/home")
    public String processLogin(Model model, @RequestParam("username") String username,
                               @RequestParam("password") String password) {
        Optional<User> jpaFindUserName = userRepo.findByUsername(username);
        Optional<User> jpaFindEmail = userRepo.findByEmail(username);

        if (jpaFindUserName.isPresent() || jpaFindEmail.isPresent()) {
            User loginUser = jpaFindUserName.orElseGet(jpaFindEmail::get);
            BCryptPasswordEncoder b = new BCryptPasswordEncoder();
            if (b.matches(password, loginUser.getPassword())) {
                model.addAttribute("currentUser", loginUser);
                return homePage();
            }
        }

        return "redirect:login?error";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:login?logout";
    }

    @PostMapping("/home/course")
    public String listCourses(Model model) {
        List<Course> listCourses = courseRepo.findAll();
        model.addAttribute("listCourses", listCourses);

        return "course-list";
    }
}