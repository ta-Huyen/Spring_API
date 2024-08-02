package com.example.spring.controller;

import com.example.spring.entity.User;
import com.example.spring.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class SignupController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/sign-up")
    public String signup(Model model) {
        model.addAttribute("user", new User());

        return "sign-up";
    }

    @PostMapping("/process-register")
    public String processRegister(User user, BindingResult bindingResult) {
        Optional<User> findByMail = userRepo.findByEmail(user.getEmail());
        Optional<User> findByUserName = userRepo.findByUsername(user.getUsername());

        if (findByUserName.isPresent()) {
            bindingResult.rejectValue("username", "error.username", "Username has already existed!");
        }
        if (findByMail.isPresent()) {
            bindingResult.rejectValue("email", "error.email", "Email has already been used!");
        }
        if (bindingResult.hasErrors()){
            return "sign-up";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepo.save(user);

        return "register-success";
    }
}