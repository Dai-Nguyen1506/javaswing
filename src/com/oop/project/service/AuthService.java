package com.oop.project.service;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Role;
import com.oop.project.model.User;
import com.oop.project.repository.UserRepository;
import com.oop.project.util.AppPaths;
import com.oop.project.util.PasswordEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void ensureDefaultUsersExist() {
        if (!userRepository.findAll().isEmpty()) {
            return;
        }
        createUser("admin", "admin123", Role.ADMIN);
        createUser("staff", "staff123", Role.STAFF);
    }

    public void createUser(String username, String rawPassword, Role role) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ValidationException("Password is required");
        }
        String saltHex = PasswordEncoder.newSaltHex();
        String hashHex = PasswordEncoder.hashPasswordHex(saltHex, rawPassword);
        userRepository.save(new User(username.trim(), role, saltHex, hashHex));
    }

    public Optional<User> login(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return Optional.empty();
        }
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        String candidate = PasswordEncoder.hashPasswordHex(user.getSaltHex(), rawPassword);
        if (!candidate.equalsIgnoreCase(user.getPasswordHashHex())) {
            return Optional.empty();
        }

        Session.setCurrentUser(user);
        audit("LOGIN", user.getUsername());
        return Optional.of(user);
    }

    public void logout() {
        Session.currentUser().ifPresent(u -> audit("LOGOUT", u.getUsername()));
        Session.clear();
    }

    private void audit(String action, String username) {
        String line = Instant.now() + "\t" + action + "\t" + username + System.lineSeparator();
        try {
            Files.writeString(AppPaths.auditLogFile(), line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Avoid breaking user flow for audit errors
            System.err.println("Audit log failed: " + e.getMessage());
        }
    }
}
