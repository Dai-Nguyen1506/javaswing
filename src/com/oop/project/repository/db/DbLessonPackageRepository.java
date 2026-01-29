package com.oop.project.repository.db;

import com.oop.project.model.LessonPackage;
import com.oop.project.repository.LessonPackageRepository;
import com.oop.project.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database implementation of LessonPackageRepository using PostgreSQL
 */
public class DbLessonPackageRepository implements LessonPackageRepository {
    private final DatabaseConnection dbConnection;

    public DbLessonPackageRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public List<LessonPackage> findAll() {
        List<LessonPackage> packages = new ArrayList<>();
        String sql = "SELECT lesson_id, lesson_name, lesson_fee, instructor_id FROM lesson ORDER BY lesson_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LessonPackage lessonPackage = extractLessonPackageFromResultSet(rs);
                packages.add(lessonPackage);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all lesson packages: " + e.getMessage());
        }
        return packages;
    }

    @Override
    public Optional<LessonPackage> findById(String packageId) {
        String sql = "SELECT lesson_id, lesson_name, lesson_fee, instructor_id FROM lesson WHERE lesson_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(packageId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                LessonPackage lessonPackage = extractLessonPackageFromResultSet(rs);
                return Optional.of(lessonPackage);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding lesson package by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<LessonPackage> findByInstructor(String instructorId) {
        List<LessonPackage> packages = new ArrayList<>();
        String sql = "SELECT lesson_id, lesson_name, lesson_fee, instructor_id FROM lesson " +
                     "WHERE instructor_id = ? ORDER BY lesson_name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(instructorId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LessonPackage lessonPackage = extractLessonPackageFromResultSet(rs);
                packages.add(lessonPackage);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding lesson packages by instructor: " + e.getMessage());
        }
        return packages;
    }

    @Override
    public void save(LessonPackage lessonPackage) {
        if (lessonPackage.getPackageId() == null || lessonPackage.getPackageId().isEmpty()) {
            insert(lessonPackage);
        } else {
            update(lessonPackage);
        }
    }

    private void insert(LessonPackage lessonPackage) {
        String sql = "INSERT INTO lesson (lesson_name, lesson_fee, instructor_id) VALUES (?, ?, ?) RETURNING lesson_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lessonPackage.getName());
            pstmt.setDouble(2, lessonPackage.getPrice());
            
            if (lessonPackage.getInstructorId() != null && !lessonPackage.getInstructorId().isEmpty()) {
                pstmt.setInt(3, Integer.parseInt(lessonPackage.getInstructorId()));
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                lessonPackage.setPackageId(String.valueOf(rs.getInt("lesson_id")));
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error inserting lesson package: " + e.getMessage());
        }
    }

    private void update(LessonPackage lessonPackage) {
        String sql = "UPDATE lesson SET lesson_name = ?, lesson_fee = ?, instructor_id = ? WHERE lesson_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, lessonPackage.getName());
            pstmt.setDouble(2, lessonPackage.getPrice());
            
            if (lessonPackage.getInstructorId() != null && !lessonPackage.getInstructorId().isEmpty()) {
                pstmt.setInt(3, Integer.parseInt(lessonPackage.getInstructorId()));
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            
            pstmt.setInt(4, Integer.parseInt(lessonPackage.getPackageId()));

            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error updating lesson package: " + e.getMessage());
        }
    }

    @Override
    public void delete(String packageId) {
        String sql = "DELETE FROM lesson WHERE lesson_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(packageId));
            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting lesson package: " + e.getMessage());
        }
    }

    public void saveAll(List<LessonPackage> packages) {
        for (LessonPackage lessonPackage : packages) {
            save(lessonPackage);
        }
    }

    private LessonPackage extractLessonPackageFromResultSet(ResultSet rs) throws SQLException {
        LessonPackage lessonPackage = new LessonPackage();
        lessonPackage.setPackageId(String.valueOf(rs.getInt("lesson_id")));
        lessonPackage.setName(rs.getString("lesson_name"));
        lessonPackage.setPrice(rs.getDouble("lesson_fee"));
        
        int instructorId = rs.getInt("instructor_id");
        if (!rs.wasNull()) {
            lessonPackage.setInstructorId(String.valueOf(instructorId));
        }
        
        lessonPackage.setDescription(""); // Description not in DB schema
        lessonPackage.setDurationMinutes(60); // Default duration
        return lessonPackage;
    }
}
