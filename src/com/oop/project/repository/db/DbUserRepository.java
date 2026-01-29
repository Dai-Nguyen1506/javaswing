package com.oop.project.repository.db;

import com.oop.project.model.Role;
import com.oop.project.model.User;
import com.oop.project.repository.UserRepository;
import com.oop.project.util.DatabaseConnection;
import com.oop.project.util.PasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database implementation of UserRepository using PostgreSQL
 * Supports both plain text passwords (from database) and hashed passwords
 */
public class DbUserRepository implements UserRepository {
    private final DatabaseConnection dbConnection;

    public DbUserRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_name, user_password, user_role FROM users ORDER BY user_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String username = rs.getString("user_name");
                String password = rs.getString("user_password");
                String roleStr = rs.getString("user_role");
                
                Role role = Role.valueOf(roleStr.toUpperCase());
                
                // Create User with plain text password stored in database
                // We'll use empty salt to indicate plain text password
                User user = new User(username, role, "", password);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT user_name, user_password, user_role FROM users WHERE user_name = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("user_name");
                String password = rs.getString("user_password");
                String roleStr = rs.getString("user_role");
                
                Role role = Role.valueOf(roleStr.toUpperCase());
                
                // Create User with plain text password stored in database
                User user = new User(name, role, "", password);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (user_name, user_password, user_role) VALUES (?, ?, ?::user_role_enum) " +
                     "ON CONFLICT (user_name) DO UPDATE SET user_password = ?, user_role = ?::user_role_enum";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Convert Java enum to PostgreSQL enum format (Title Case)
            String pgRoleValue = user.getRole().name().charAt(0) + user.getRole().name().substring(1).toLowerCase();
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHashHex());
            pstmt.setString(3, pgRoleValue);
            pstmt.setString(4, user.getPasswordHashHex());
            pstmt.setString(5, pgRoleValue);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    public void delete(String username) {
        String sql = "DELETE FROM users WHERE user_name = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    public void saveAll(List<User> users) {
        for (User user : users) {
            save(user);
        }
    }
}
