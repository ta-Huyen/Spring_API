package com.example.spring.service;

import com.example.spring.entity.User;
import com.example.spring.exception.ResourceNotFoundException;
import com.example.spring.repository.UserRepository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    PasswordEncoder encoder;
//    private final RedisTemplate<String, User> redisTemplate;

    public Boolean existsByUsername(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    public Boolean existsByEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    public User findUserById(Long id) {
//        String key = "user_" + id;
//        ValueOperations<String, User> operations = redisTemplate.opsForValue();
//        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
//        if (hasKey) {
//            User user = operations.get(key);
//            log.info("UserServiceImpl.findUserById() : cache user >> {}", user.toString());
//            return user;
//        }
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
//            operations.set(key, user.get(), 10, TimeUnit.SECONDS);
//            log.info("UserServiceImpl.findUserById() : cache insert >> {}", user.get());
            return user.get();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public Page<User> findPaginated(int pageNo, int size) {
        return userRepo.findAll(PageRequest.of(pageNo-1, size));
    }

    public User saveUser(User requestSave) {
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

        return userRepo.save(user);
    }

    public User updateUser(User user) {
//        final String key = "user_" + user.getId();
//        final boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
//        if (hasKey) {
//            redisTemplate.delete(key);
//            log.info("UserServiceImpl.updateUser() : cache update >> {}", user);
//        }
        return userRepo.save(user);
    }

    public void deleteUser(Long id) {
//        final String key = "user_" + id;
//        final boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
//        if (hasKey) {
//            redisTemplate.delete(key);
//            log.info("UserServiceImpl.deleteUser() : cache delete ID >> {}", id);
//        }
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            userRepo.deleteById(id);
        } else {
            throw new ResourceNotFoundException();
        }
    }
}
