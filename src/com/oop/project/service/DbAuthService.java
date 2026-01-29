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

/**
 * Authentication Service that supports both hashed and plain text passwords
 * Used for database authentication where passwords may be stored as plain text
 */
public class DbAuthService extends AuthService {
    
    public DbAuthService(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public Optional<User> login(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return Optional.empty();
        }
        
        Optional<User> userOpt = getUserRepository().findByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // Check if password is hashed (has salt) or plain text
        boolean isAuthenticated = false;
        
        if (user.getSaltHex() == null || user.getSaltHex().isEmpty()) {
            // Plain text password comparison (for database users)
            isAuthenticated = user.getPasswordHashHex().equals(rawPassword);
        } else {
            // Hashed password comparison (for file-based users)
            String candidate = PasswordEncoder.hashPasswordHex(user.getSaltHex(), rawPassword);
            isAuthenticated = candidate.equalsIgnoreCase(user.getPasswordHashHex());
        }
        
        if (!isAuthenticated) {
            return Optional.empty();
        }

        Session.setCurrentUser(user);
        audit("LOGIN", user.getUsername());
        return Optional.of(user);
    }
    
    // Allow access to userRepository for subclass
    protected UserRepository getUserRepository() {
        // Use reflection to access private field (not ideal but works)
        try {
            java.lang.reflect.Field field = AuthService.class.getDeclaredField("userRepository");
            field.setAccessible(true);
            return (UserRepository) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Cannot access userRepository", e);
        }
    }
    
    private void audit(String action, String username) {
        String line = Instant.now() + "\t" + action + "\t" + username + System.lineSeparator();
        try {
            Files.writeString(AppPaths.auditLogFile(), line, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Audit log failed: " + e.getMessage());
        }
    }
}
