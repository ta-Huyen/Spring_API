//package com.example.spring.service;
//
//import com.example.spring.entity.User;
//import com.example.spring.repository.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//@Component
//@Slf4j
//public class DataInitializer implements CommandLineRunner {
//    //...
//    @Autowired
//    UserRepository users;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//    @Override
//    public void run(String... args) throws Exception {
//        //...
//        this.users.save(User.builder()
//                .username("user")
//                .name("simple user")
//                .email("simple@mail.com")
//                .password(this.passwordEncoder.encode("password"))
//                .roles(Arrays.asList( "ROLE_USER"))
//                .build()
//        );
//        this.users.save(User.builder()
//                .username("admin")
//                .name("simple admin")
//                .email("admin@mail.com")
//                .password(this.passwordEncoder.encode("password"))
//                .roles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"))
//                .build()
//        );
//        log.debug("printing all users...");
//        this.users.findAll().forEach(v -> log.debug(" User :" + v.toString()));
//    }
//}
