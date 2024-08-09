package com.example.spring.repository;

import com.example.spring.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM User u WHERE " +
            "(:id IS NULL OR u.id = :id) AND " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))AND " +
            "(:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT(:username, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT(:email, '%')))", nativeQuery = true)
    List<User> findByCriteria(@Param("id") String id, @Param("name") String name,
                              @Param("username") String username, @Param("email") String email);
}