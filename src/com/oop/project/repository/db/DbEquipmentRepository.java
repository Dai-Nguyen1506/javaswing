package com.oop.project.repository.db;

import com.oop.project.model.Equipment;
import com.oop.project.model.EquipmentCondition;
import com.oop.project.repository.EquipmentRepository;
import com.oop.project.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database implementation of EquipmentRepository using PostgreSQL
 */
public class DbEquipmentRepository implements EquipmentRepository {
    private final DatabaseConnection dbConnection;

    public DbEquipmentRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public List<Equipment> findAll() {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT equipment_id, equipment_name, equipment_fee, equipment_number, equipment_available " +
                     "FROM equipment ORDER BY equipment_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Equipment equipment = extractEquipmentFromResultSet(rs);
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all equipment: " + e.getMessage());
        }
        return equipmentList;
    }

    @Override
    public Optional<Equipment> findById(String equipmentId) {
        String sql = "SELECT equipment_id, equipment_name, equipment_fee, equipment_number, equipment_available " +
                     "FROM equipment WHERE equipment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(equipmentId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Equipment equipment = extractEquipmentFromResultSet(rs);
                return Optional.of(equipment);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding equipment by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Equipment> findByCategory(String category) {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT equipment_id, equipment_name, equipment_fee, equipment_number, equipment_available " +
                     "FROM equipment WHERE equipment_name LIKE ? ORDER BY equipment_name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + category + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Equipment equipment = extractEquipmentFromResultSet(rs);
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error finding equipment by category: " + e.getMessage());
        }
        return equipmentList;
    }

    public List<Equipment> findAvailable() {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT equipment_id, equipment_name, equipment_fee, equipment_number, equipment_available " +
                     "FROM equipment WHERE equipment_available > 0 ORDER BY equipment_name";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Equipment equipment = extractEquipmentFromResultSet(rs);
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error finding available equipment: " + e.getMessage());
        }
        return equipmentList;
    }

    @Override
    public void save(Equipment equipment) {
        if (equipment.getEquipmentId() == null || equipment.getEquipmentId().isEmpty()) {
            insert(equipment);
        } else {
            update(equipment);
        }
    }

    private void insert(Equipment equipment) {
        String sql = "INSERT INTO equipment (equipment_name, equipment_fee, equipment_number, equipment_available) " +
                     "VALUES (?, ?, ?, ?) RETURNING equipment_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipment.getName());
            pstmt.setDouble(2, equipment.getFee()); // Use fee from equipment object
            pstmt.setInt(3, equipment.getQuantity());
            pstmt.setInt(4, equipment.getAvailable());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                equipment.setEquipmentId(String.valueOf(rs.getInt("equipment_id")));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting equipment: " + e.getMessage());
            throw new RuntimeException("Failed to insert equipment: " + e.getMessage(), e);
        }
    }

    private void update(Equipment equipment) {
        String sql = "UPDATE equipment SET equipment_name = ?, equipment_fee = ?, equipment_number = ?, equipment_available = ? " +
                     "WHERE equipment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, equipment.getName());
            pstmt.setDouble(2, equipment.getFee());
            pstmt.setInt(3, equipment.getQuantity());
            pstmt.setInt(4, equipment.getAvailable());
            pstmt.setInt(5, Integer.parseInt(equipment.getEquipmentId()));

            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error updating equipment: " + e.getMessage());
            throw new RuntimeException("Failed to update equipment: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String equipmentId) {
        String sql = "DELETE FROM equipment WHERE equipment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(equipmentId));
            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting equipment: " + e.getMessage());
        }
    }

    public void saveAll(List<Equipment> equipmentList) {
        for (Equipment equipment : equipmentList) {
            save(equipment);
        }
    }

    public void updateAvailability(String equipmentId, int newAvailable) {
        String sql = "UPDATE equipment SET equipment_available = ? WHERE equipment_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newAvailable);
            pstmt.setInt(2, Integer.parseInt(equipmentId));
            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error updating equipment availability: " + e.getMessage());
        }
    }

    private Equipment extractEquipmentFromResultSet(ResultSet rs) throws SQLException {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(String.valueOf(rs.getInt("equipment_id")));
        equipment.setName(rs.getString("equipment_name"));
        equipment.setCategory(rs.getString("equipment_name")); // Using name as category
        equipment.setQuantity(rs.getInt("equipment_number"));
        equipment.setAvailable(rs.getInt("equipment_available"));
        equipment.setCondition(EquipmentCondition.GOOD); // Default condition
        equipment.setFee(rs.getDouble("equipment_fee")); // Get fee from database
        return equipment;
    }
}
