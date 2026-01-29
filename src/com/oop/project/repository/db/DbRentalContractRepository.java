package com.oop.project.repository.db;

import com.oop.project.model.ContractStatus;
import com.oop.project.model.RentalContract;
import com.oop.project.repository.RentalContractRepository;
import com.oop.project.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database implementation of RentalContractRepository using PostgreSQL
 */
public class DbRentalContractRepository implements RentalContractRepository {
    private final DatabaseConnection dbConnection;

    public DbRentalContractRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public List<RentalContract> findAll() {
        List<RentalContract> contracts = new ArrayList<>();
        String sql = "SELECT rental_id, rental_duration, rental_fee, rental_status, " +
                     "lesson_id, customer_id, equipment_id FROM rental_contract ORDER BY rental_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RentalContract contract = extractRentalContractFromResultSet(rs);
                contracts.add(contract);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all rental contracts: " + e.getMessage());
        }
        return contracts;
    }

    @Override
    public Optional<RentalContract> findByContractNumber(String contractNumber) {
        String sql = "SELECT rental_id, rental_duration, rental_fee, rental_status, " +
                     "lesson_id, customer_id, equipment_id FROM rental_contract WHERE rental_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(contractNumber));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                RentalContract contract = extractRentalContractFromResultSet(rs);
                return Optional.of(contract);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding rental contract by number: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<RentalContract> findByCustomerId(String customerId) {
        List<RentalContract> contracts = new ArrayList<>();
        String sql = "SELECT rental_id, rental_duration, rental_fee, rental_status, " +
                     "lesson_id, customer_id, equipment_id FROM rental_contract " +
                     "WHERE customer_id = ? ORDER BY rental_id DESC";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RentalContract contract = extractRentalContractFromResultSet(rs);
                contracts.add(contract);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding rental contracts by customer: " + e.getMessage());
        }
        return contracts;
    }

    @Override
    public List<RentalContract> findByStatus(ContractStatus status) {
        List<RentalContract> contracts = new ArrayList<>();
        String sql = "SELECT rental_id, rental_duration, rental_fee, rental_status, " +
                     "lesson_id, customer_id, equipment_id FROM rental_contract " +
                     "WHERE rental_status = ?::rental_status_enum ORDER BY rental_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Convert Java enum to PostgreSQL enum format (Title Case)
            String pgEnumValue = status.name().charAt(0) + status.name().substring(1).toLowerCase();
            pstmt.setString(1, pgEnumValue);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RentalContract contract = extractRentalContractFromResultSet(rs);
                contracts.add(contract);
            }
        } catch (SQLException e) {
            System.err.println("Error finding rental contracts by status: " + e.getMessage());
        }
        return contracts;
    }

    @Override
    public void save(RentalContract contract) {
        if (contract.getContractNumber() == null || contract.getContractNumber().isEmpty()) {
            insert(contract);
        } else {
            update(contract);
        }
    }

    private void insert(RentalContract contract) {
        String sql = "INSERT INTO rental_contract (rental_duration, rental_fee, rental_status, " +
                     "lesson_id, customer_id, equipment_id) VALUES (?, ?, ?::rental_status_enum, ?, ?, ?) " +
                     "RETURNING rental_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, contract.getDurationMinutes());
            pstmt.setDouble(2, contract.getRentalFee());
            // Convert Java enum to PostgreSQL enum format (Title Case)
            String pgEnumValue = contract.getStatus().name().charAt(0) + contract.getStatus().name().substring(1).toLowerCase();
            pstmt.setString(3, pgEnumValue);
            
            if (contract.getLessonPackageId() != null && !contract.getLessonPackageId().isEmpty()) {
                pstmt.setInt(4, Integer.parseInt(contract.getLessonPackageId()));
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            pstmt.setInt(5, Integer.parseInt(contract.getCustomerId()));
            pstmt.setInt(6, Integer.parseInt(contract.getEquipmentId()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                contract.setContractNumber(String.valueOf(rs.getInt("rental_id")));
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error inserting rental contract: " + e.getMessage());
        }
    }

    private void update(RentalContract contract) {
        String sql = "UPDATE rental_contract SET rental_duration = ?, rental_fee = ?, " +
                     "rental_status = ?::rental_status_enum, lesson_id = ?, customer_id = ?, equipment_id = ? " +
                     "WHERE rental_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, contract.getDurationMinutes());
            pstmt.setDouble(2, contract.getRentalFee());
            // Convert Java enum to PostgreSQL enum format (Title Case)
            String pgEnumValue = contract.getStatus().name().charAt(0) + contract.getStatus().name().substring(1).toLowerCase();
            pstmt.setString(3, pgEnumValue);
            
            if (contract.getLessonPackageId() != null && !contract.getLessonPackageId().isEmpty()) {
                pstmt.setInt(4, Integer.parseInt(contract.getLessonPackageId()));
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            pstmt.setInt(5, Integer.parseInt(contract.getCustomerId()));
            pstmt.setInt(6, Integer.parseInt(contract.getEquipmentId()));
            pstmt.setInt(7, Integer.parseInt(contract.getContractNumber()));

            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error updating rental contract: " + e.getMessage());
        }
    }

    @Override
    public void delete(String contractNumber) {
        String sql = "DELETE FROM rental_contract WHERE rental_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(contractNumber));
            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting rental contract: " + e.getMessage());
        }
    }

    public void saveAll(List<RentalContract> contracts) {
        for (RentalContract contract : contracts) {
            save(contract);
        }
    }

    public List<RentalContract> findOverdue() {
        return findByStatus(ContractStatus.OVERDUE);
    }

    public List<RentalContract> findActive() {
        return findByStatus(ContractStatus.ACTIVE);
    }

    private RentalContract extractRentalContractFromResultSet(ResultSet rs) throws SQLException {
        RentalContract contract = new RentalContract();
        contract.setContractNumber(String.valueOf(rs.getInt("rental_id")));
        contract.setDurationMinutes(rs.getInt("rental_duration"));
        contract.setRentalFee(rs.getDouble("rental_fee"));
        
        String statusStr = rs.getString("rental_status");
        // Convert PostgreSQL enum format to Java enum (UPPERCASE)
        contract.setStatus(ContractStatus.valueOf(statusStr.toUpperCase()));
        
        int lessonId = rs.getInt("lesson_id");
        if (!rs.wasNull()) {
            contract.setLessonPackageId(String.valueOf(lessonId));
        }
        
        contract.setCustomerId(String.valueOf(rs.getInt("customer_id")));
        contract.setEquipmentId(String.valueOf(rs.getInt("equipment_id")));
        
        // Default values for fields not in DB
        contract.setStartTime(LocalDateTime.now());
        contract.setLessonFee(0.0);
        
        return contract;
    }
}
