package com.example.spring.service;

import com.example.spring.entity.User;
import com.example.spring.exception.ResourceNotFoundException;
import com.example.spring.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;

    public User findByUsername(String username) {
        return userRepo.findByUsername(username).orElse(null);
    }

    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    public User findUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    private String reassignValue(String str) {
        return str.isBlank() ? null : str;
    }

    public List<User> findUser(String id, String name, String username, String email, List<String> roles) {
        List<User> userByCriteria = userRepo.findByCriteria(reassignValue(id), reassignValue(name),
                reassignValue(username), reassignValue(email));

        List<String> arrRoles = new ArrayList<>();
        roles.forEach(r -> {
            switch (r) {
                case "admin":
                    arrRoles.add("ROLE_ADMIN");
                    break;
                case "mod":
                    arrRoles.add("ROLE_MODERATOR");
                    break;
                case "user":
                    arrRoles.add("ROLE_USER");
                    break;
            }
        });
        int i=0;
        while (i < userByCriteria.size()) {
            if (!new HashSet<>(userByCriteria.get(i).getRoles()).containsAll(arrRoles)) {
                userByCriteria.remove(i);
            } else {
                i += 1;
            }
        }

        return userByCriteria;
    }

    public Page<User> findPaginated(int pageNo, int size) {
        return userRepo.findAll(PageRequest.of(pageNo-1, size));
    }

    public void addUser(User requestSave) {
        List<String> requestRoles = requestSave.getRoles();
        List<String> roles;

        if (requestRoles == null || requestRoles.isEmpty()) {
            roles = List.of("ROLE_USER");
        } else {
            roles = new ArrayList<>();
            requestRoles.forEach(strRole -> {
                switch (strRole) {
                    case "admin":
                        roles.add("ROLE_ADMIN");
                        break;
                    case "mod":
                        roles.add("ROLE_MODERATOR");
                        break;
                    default:
                        roles.add("ROLE_USER");
                }
            });
        }

        User user = new User();
        user.setName(requestSave.getName());
        user.setUsername(requestSave.getUsername());
        user.setEmail(requestSave.getEmail());
        user.setPassword(encoder.encode(requestSave.getPassword()));
        user.setRoles(roles);

        userRepo.save(user);
    }

    public void editUser(long id, User request) {
        User userById = findUserById(id);
        if (userById != null) {
            userById.setName(request.getName());
            userById.setUsername(request.getUsername());
            userById.setEmail(request.getEmail());
            userById.setRoles(request.getRoles());

            userRepo.save(userById);
        } else {
            log.error("Error: ", new ResourceNotFoundException("User with id " + id + " not found"));
        }
    }

    public void resetPassword(User user, String password) {
        user.setPassword(encoder.encode(password));
        userRepo.save(user);
    }

    public void deleteUser(Long id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            userRepo.deleteById(id);
            log.info("Delete user with id " + id);
        } else {
            log.error("Error: ", new ResourceNotFoundException("User with id " + id + "not found"));
        }
    }
}
