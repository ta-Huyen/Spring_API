package com.example.spring.controller;

import com.example.spring.entity.LoginRequest;
import com.example.spring.entity.User;
import com.example.spring.exception.ResourceNotFoundException;
import com.example.spring.repository.UserRepository;
import com.example.spring.security.JwtTokenProvider;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserRepository userRepo;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<Object, Object> model = new HashMap<>();
        model.put("username", user);
        model.put("token", token);
        return ResponseEntity.ok(model);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResourceNotFoundException("Error: Username is already taken!"));
        }
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new ResourceNotFoundException("Error: Email is already in use!"));
        }

        // Create new user's account
        List<String> strRoles = user.getRoles();

        if (strRoles == null || strRoles.isEmpty()) {
            strRoles= List.of("ROLE_USER");
        }

        user.setRoles(strRoles);
        userRepo.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}

