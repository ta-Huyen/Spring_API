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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    PasswordEncoder encoder;
    public static User loggedUser;

    public static User getLoggedUser() {
        return loggedUser;
    }

    @GetMapping("/home")
    public ModelAndView showHomePage(Model model) {
        if (loggedUser != null) {
            model.addAttribute("currentUser", loggedUser);
            return new ModelAndView("home-page");
        }
        return showLogin(model);
    }

    @GetMapping("/login")
    public ModelAndView showLogin(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());

        return new ModelAndView("login");
    }

    @GetMapping("/sign-up")
    public ModelAndView showSignup(Model model) {
        model.addAttribute("user", new User());

        return new ModelAndView("sign-up");
    }

    @GetMapping("/forgot-password")
    public ModelAndView showForgotPassword() {
        return new ModelAndView("forgot-password");
    }

    @GetMapping("/reset-password")
    public ModelAndView showResetPassword(Model model) {
        return new ModelAndView("reset-password");
    }

    @PostMapping("/home")
    public ModelAndView login(@Valid LoginRequest loginRequest, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateJwtToken(authentication);

            User user = (User) authentication.getPrincipal();
            List<String> roles = user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            model.addAttribute("currentUser", user);
            model.addAttribute("token", token);
            loggedUser = user;
            return showHomePage(model);
        } catch (AuthenticationException e) {
            SecurityContextHolder.getContext().setAuthentication(null);
            log.error("Error: ", new BadCredentialsException("Invalid username/password supplied"));
            return new ModelAndView("redirect:login?error");
        }
    }

    @PostMapping("/process-register")
    public ModelAndView registerUser(@Valid User signUpRequest, BindingResult bindingResult) {
        if (userService.findByUsername(signUpRequest.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.username", "Username is already taken!");
            log.error("Error: ", new BadRequestException("Username is already taken"));
        }
        if (userService.findUserByEmail(signUpRequest.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.email", "Email is already in use!");
            log.error("Error: ", new BadRequestException("Email is already in use!"));
        }
        if (bindingResult.hasErrors()){
            return new ModelAndView("sign-up");
        }

        userService.addUser(signUpRequest);

        log.info("User registered successfully!");
        return new ModelAndView("register-success");
    }

    @GetMapping("/reset/{id}")
    public ModelAndView checkID(@PathVariable("id") Long id, Model model) {
        User userById = userService.findUserById(id);
        if (userById != null) {
            model.addAttribute("userByEmail", userById);
            return showResetPassword(model);
        }

        return showLogin(model);
    }

    @PostMapping("/check-email")
    public ModelAndView checkEmail(Model model, @RequestParam String email) {
        User userByEmail = userService.findUserByEmail(email);
        if (userByEmail != null) {
            model.addAttribute("userByEmail", userByEmail);
            return showResetPassword(model);
        }

        return new ModelAndView("redirect:forgot-password?error");
    }

    @PostMapping("/reset-password/{id}")
    public ModelAndView resetPassword(@PathVariable("id") long id, @ModelAttribute("userByEmail") User request,
                                      Model model, BindingResult bindingResult) {
        User user = userService.findUserById(id);
        if (user == null) {
            model.addAttribute("idNotFound", id);
        } else if (encoder.matches(request.getPassword(), user.getPassword())) {
            bindingResult.rejectValue("password", "error.password", "New password must be " +
                    "different from current password.");
            log.error("Error: ", new BadRequestException("New password must be different from current password."));
        }
        if ((user == null) || bindingResult.hasErrors()) {
            return showResetPassword(model);
        }
        userService.resetPassword(user, request.getPassword());

        return showLogin(model);
    }

    @PostMapping("/delete")
    public ModelAndView delete(Model model) {
        userService.deleteUser(loggedUser.getId());
        loggedUser = null;

        return showLogin(model);
    }

    @PostMapping("/logout")
    public ModelAndView logout() {
        loggedUser = null;
        log.info("User logout!");
        return new ModelAndView("redirect:login?logout");
    }
}