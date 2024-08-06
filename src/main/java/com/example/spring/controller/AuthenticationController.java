package com.example.spring.controller;

import com.example.spring.entity.LoginRequest;
import com.example.spring.entity.User;
import com.example.spring.security.JwtTokenProvider;

import com.example.spring.service.UserService;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserService userService;

    @GetMapping("/home")
    public ModelAndView showHomePage() {
        return new ModelAndView("/home-page");
    }

    @GetMapping("/login")
    public ModelAndView showLogin(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());

        return new ModelAndView("/login");
    }

    @GetMapping("/sign-up")
    public ModelAndView showSignup(Model model) {
        model.addAttribute("user", new User());

        return new ModelAndView("sign-up");
    }

    @PostMapping("/home")
    public ModelAndView login(@Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateJwtToken(authentication);

            User user = (User) authentication.getPrincipal();
            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            Map<Object, Object> model = new HashMap<>();
            model.put("user", user);
            model.put("token", token);
            return showHomePage();
        } catch (AuthenticationException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            log.error("Error: ", new BadCredentialsException("Invalid username/password supplied"));
            return new ModelAndView("redirect:login?error");
        }
    }

    @PostMapping("/process-register")
    public ModelAndView registerUser(@Valid User signUpRequest, BindingResult bindingResult) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username is already taken!");
            log.error("Error: ", new BadRequestException("Username is already taken"));
        }
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email is already in use!");
            log.error("Error: ", new BadRequestException("Email is already in use!"));
        }
        if (bindingResult.hasErrors()){
            return new ModelAndView("sign-up");
        }

        userService.saveUser(signUpRequest);

        log.info("User registered successfully!");
        return new ModelAndView("/register-success");
    }

    @PostMapping("/logout")
    public ModelAndView logout() {
        log.info("User logout!");
        return new ModelAndView("redirect:login?logout");
    }
}