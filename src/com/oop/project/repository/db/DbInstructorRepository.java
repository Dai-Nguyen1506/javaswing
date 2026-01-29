package com.oop.project.repository.db;

import com.oop.project.model.Instructor;
import com.oop.project.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database implementation for Instructor repository using PostgreSQL
 */
public class DbInstructorRepository {
    private final DatabaseConnection dbConnection;

    public DbInstructorRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Instructor> findAll() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT instructor_id, instructor_name, instructor_email, instructor_phone_number " +
                     "FROM instructor ORDER BY instructor_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Instructor instructor = extractInstructorFromResultSet(rs);
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all instructors: " + e.getMessage());
        }
        return instructors;
    }

    public Optional<Instructor> findById(String instructorId) {
        String sql = "SELECT instructor_id, instructor_name, instructor_email, instructor_phone_number " +
                     "FROM instructor WHERE instructor_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(instructorId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Instructor instructor = extractInstructorFromResultSet(rs);
                return Optional.of(instructor);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding instructor by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void save(Instructor instructor) {
        if (instructor.getInstructorId() == null || instructor.getInstructorId().isEmpty()) {
            insert(instructor);
        } else {
            update(instructor);
        }
    }

    private void insert(Instructor instructor) {
        String sql = "INSERT INTO instructor (instructor_name, instructor_email, instructor_phone_number) " +
                     "VALUES (?, ?, ?) RETURNING instructor_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, instructor.getName());
            pstmt.setString(2, instructor.getSpecialty()); // Using specialty as email
            pstmt.setString(3, instructor.getPhone());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                instructor.setInstructorId(String.valueOf(rs.getInt("instructor_id")));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting instructor: " + e.getMessage());
        }
    }

    private void update(Instructor instructor) {
        String sql = "UPDATE instructor SET instructor_name = ?, instructor_email = ?, " +
                     "instructor_phone_number = ? WHERE instructor_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, instructor.getName());
            pstmt.setString(2, instructor.getSpecialty());
            pstmt.setString(3, instructor.getPhone());
            pstmt.setInt(4, Integer.parseInt(instructor.getInstructorId()));

            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error updating instructor: " + e.getMessage());
        }
    }

    public void delete(String instructorId) {
        String sql = "DELETE FROM instructor WHERE instructor_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(instructorId));
            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting instructor: " + e.getMessage());
        }
    }

    private Instructor extractInstructorFromResultSet(ResultSet rs) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setInstructorId(String.valueOf(rs.getInt("instructor_id")));
        instructor.setName(rs.getString("instructor_name"));
        instructor.setSpecialty(rs.getString("instructor_email"));
        instructor.setPhone(rs.getString("instructor_phone_number"));
        return instructor;
    }
}
