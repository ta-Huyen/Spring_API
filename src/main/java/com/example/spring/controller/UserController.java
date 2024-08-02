package com.example.spring.controller;

import com.example.spring.entity.User;
import com.example.spring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/userRepo")
public class UserController {
    protected static final String DEFAULT_PAGE_SIZE = "100";
    protected static final String DEFAULT_PAGE_NUM = "0";

    private final UserService userService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody User user,
                           HttpServletRequest request, HttpServletResponse response) {
        final User createUser = userService.saveUser(user);
        response.setHeader("Location", request.getRequestURL().append("/").append(createUser.getId()).toString());
    }

    @GetMapping("/me")
    public ResponseEntity currentUser(@AuthenticationPrincipal UserDetails userDetails){
        Map<Object, Object> model = new HashMap<>();
        model.put("username", userDetails.getUsername());
        model.put("roles", userDetails.getAuthorities()
                .stream()
                .map(a -> ((GrantedAuthority) a).getAuthority())
                .collect(toList())
        );
        return ok(model);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User findOneUser(@PathVariable("id") Long id) {
        log.info("User UserController {}", userService.findUserById(id));
        return userService.findUserById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Page<User> getAllUsers(@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUM) Integer page, @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) Integer size) {
        return userService.getAllUsers(page, size);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyUser(@PathVariable("id") Long id, @RequestBody User user) {
        userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }
}