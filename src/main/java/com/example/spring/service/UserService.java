package com.example.spring.service;

import com.example.spring.entity.User;
import com.example.spring.exception.ResourceNotFoundException;
import com.example.spring.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, User> redisTemplate;

    public User findUserById(Long id) {
        final String key = "user_" + id;
        final ValueOperations<String, User> operations = redisTemplate.opsForValue();
        final boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (hasKey) {
            final User user = operations.get(key);
            log.info("UserServiceImpl.findUserById() : cache user >> {}", user.toString());
            return user;
        }
        final Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            operations.set(key, user.get(), 10, TimeUnit.SECONDS);
            log.info("UserServiceImpl.findUserById() : cache insert >> {}", user.get());
            return user.get();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    public Page<User> getAllUsers(Integer page, Integer size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        final String key = "user_" + user.getId();
        final boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (hasKey) {
            redisTemplate.delete(key);
            log.info("UserServiceImpl.updateUser() : cache update >> {}", user);
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        final String key = "user_" + id;
        final boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        if (hasKey) {
            redisTemplate.delete(key);
            log.info("UserServiceImpl.deleteUser() : cache delete ID >> {}", id);
        }
        final Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException();
        }
    }
}
