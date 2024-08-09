package com.example.spring.controller;

import com.example.spring.configuration.Constant;
import com.example.spring.entity.User;
import com.example.spring.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ModelAndView showUsers(Model model) {
        return findPaginated(1, model);
    }

    @GetMapping("/edit-profile")
    public ModelAndView showEditProfile(Model model) {
        User user = AuthenticationController.getLoggedUser();
        if (user == null) {
            model.addAttribute("idNotFound", -1);
        }
        model.addAttribute("editInfo", Objects.requireNonNullElseGet(user, User::new));

        return new ModelAndView("edit-profile");
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

        return new ModelAndView("user-list");
    }

    @GetMapping("/find")
    public ModelAndView findUser(@RequestParam("byId") String id, @RequestParam("byName") String name,
                                 @RequestParam("byUsername") String username,
                                 @RequestParam("byEmail") String email, @RequestParam("byRole") List<String> roles,
                                 Model model) {
        if (!id.isBlank() && !Constant.isNumeric(id)) {
                model.addAttribute("idError", true);
        }

        List<User> listUsers = userService.findUser(id, name, username, email, roles);

        model.addAttribute("byId", id);
        model.addAttribute("byName", name);
        model.addAttribute("byUsername", username);
        model.addAttribute("byEmail", email);
        model.addAttribute("byRole", roles.toString());
        model.addAttribute("byName", name);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 0);
        model.addAttribute("totalItems", listUsers.size());
        model.addAttribute("listUsers", listUsers);

        return new ModelAndView("user-list");
    }

    @PostMapping("/edit/{id}")
    public ModelAndView editUser(@PathVariable("id") Long id, @ModelAttribute("editInfo") User request, Model model,
                      BindingResult bindingResult) {
        User userByUsername = userService.findByUsername(request.getUsername());
        User userByEmail = userService.findUserByEmail(request.getEmail());
        if ((userByUsername != null) && (!Objects.equals(userByUsername.getId(), id))) {
            bindingResult.rejectValue("username", "error.username", "Username is already taken!");
            log.error("Error: ", new BadRequestException("Username is already taken!"));
        }
        if ((userByEmail != null) && (!Objects.equals(userByEmail.getId(), id))) {
            bindingResult.rejectValue("email", "error.email", "Email is already in use!");
            log.error("Error: ", new BadRequestException("Email is already in use!"));
        }
        if (bindingResult.hasErrors()){
            return new ModelAndView("edit-profile");
        }

        userService.editUser(id, request);
        model.addAttribute("isSaved", true);
        return showEditProfile(model);
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable("id") Long id, Model model) {
        userService.deleteUser(id);
        return showUsers(model);
    }
}