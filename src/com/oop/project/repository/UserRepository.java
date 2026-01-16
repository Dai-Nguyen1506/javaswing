package com.oop.project.repository;

import com.oop.project.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);

    List<User> findAll();

    void save(User user);
}
