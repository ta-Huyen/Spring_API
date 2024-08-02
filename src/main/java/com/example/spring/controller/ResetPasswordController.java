package com.example.spring.controller;

import com.example.spring.entity.User;
import com.example.spring.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ResetPasswordController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/check-email")
    public String checkEmail(Model model, @RequestParam String email) {
        Optional<User> findByEmail = userRepo.findByEmail(email);

        if (findByEmail.isPresent()) {
            model.addAttribute("user", findByEmail);
            return "reset-password";
        }

        return "redirect:forgot-password?error";
    }

    @PostMapping("/update-password/{password}")
    public String update(User user, @PathVariable(value="password") String password) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encodedPassword = passwordEncoder.encode(password);
//        user.setPassword(encodedPassword);
//        jpaRepository.save(user);

        return "login";
    }
}
