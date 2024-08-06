package com.example.spring.controller;

import com.example.spring.configuration.Constant;
import com.example.spring.entity.User;
import com.example.spring.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal User user){
        Map<Object, Object> model = new HashMap<>();
        model.put("username", user.getUsername());
        model.put("roles", user.getAuthorities()
                .stream()
                .map(a -> ((GrantedAuthority) a).getAuthority())
                .collect(toList())
        );
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ModelAndView viewUser(Model model) {
        return findPaginated(1, model);
    }

    @GetMapping("/page/{pageNo}")
    public ModelAndView findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {
        int size = Constant.DEFAULT_PAGE_SIZE;

        Page<User> page = userService.findPaginated(pageNo, size);
        List<User> listUsers = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);

        return new ModelAndView("/user-list");
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findOneUser(@PathVariable("id") Long id) {
        log.info("User UserController {}", userService.findUserById(id));
        Map<Object, Object> model = new HashMap<>();
        model.put("user", userService.findUserById(id));
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyUser(@PathVariable("id") Long id, @RequestBody User user) {
        userService.updateUser(user);
    }

    @GetMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ModelAndView deleteUser(@PathVariable("id") Long id, Model model) {
        userService.deleteUser(id);
        return viewUser(model);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword( @PathVariable String password, @RequestBody User user) {
        user.setPassword(encoder.encode(password));
        userService.saveUser(user);

        return ResponseEntity.ok("Reset password successfully!");
    }
}