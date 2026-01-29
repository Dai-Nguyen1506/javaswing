package com.oop.project.repository.db;

import com.oop.project.model.EquipmentCondition;
import com.oop.project.model.Item;
import com.oop.project.model.ItemStatus;
import com.oop.project.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbItemRepository {
    private final DatabaseConnection dbConnection;

    public DbItemRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<Item> findByEquipmentId(String equipmentId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT item_id, item_name, item_condition, item_status, equipment_id " +
                     "FROM items WHERE equipment_id = ? ORDER BY item_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(equipmentId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Item item = extractItemFromResultSet(rs);
                items.add(item);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding items by equipment ID: " + e.getMessage());
        }
        return items;
    }

    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT item_id, item_name, item_condition, item_status, equipment_id FROM items ORDER BY item_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = extractItemFromResultSet(rs);
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all items: " + e.getMessage());
        }
        return items;
    }
    
    public java.util.Optional<Item> findById(String itemId) {
        String sql = "SELECT item_id, item_name, item_condition, item_status, equipment_id " +
                     "FROM items WHERE item_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(itemId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Item item = extractItemFromResultSet(rs);
                return java.util.Optional.of(item);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding item by ID: " + e.getMessage());
        }
        return java.util.Optional.empty();
    }
    
    public void save(Item item) {
        if (item.getItemId() == null || item.getItemId().isEmpty()) {
            insert(item);
        } else {
            update(item);
        }
    }
    
    private void insert(Item item) {
        String sql = "INSERT INTO items (item_name, item_condition, item_status, equipment_id) " +
                     "VALUES (?, ?, ?, ?) RETURNING item_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getItemName());
            
            String condition = item.getCondition().name();
            condition = condition.charAt(0) + condition.substring(1).toLowerCase().replace("_", " ");
            pstmt.setString(2, condition);
            
            String status = item.getStatus().name();
            status = status.charAt(0) + status.substring(1).toLowerCase();
            pstmt.setString(3, status);
            
            pstmt.setInt(4, Integer.parseInt(item.getEquipmentId()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                item.setItemId(String.valueOf(rs.getInt("item_id")));
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error inserting item: " + e.getMessage());
            throw new RuntimeException("Failed to insert item: " + e.getMessage(), e);
        }
    }
    
    private void update(Item item) {
        String sql = "UPDATE items SET item_name = ?, item_condition = ?, item_status = ? WHERE item_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getItemName());
            
            String condition = item.getCondition().name();
            condition = condition.charAt(0) + condition.substring(1).toLowerCase().replace("_", " ");
            pstmt.setString(2, condition);
            
            String status = item.getStatus().name();
            status = status.charAt(0) + status.substring(1).toLowerCase();
            pstmt.setString(3, status);
            
            pstmt.setInt(4, Integer.parseInt(item.getItemId()));

            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error updating item: " + e.getMessage());
            throw new RuntimeException("Failed to update item: " + e.getMessage(), e);
        }
    }
    
    public void delete(String itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(itemId));
            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting item: " + e.getMessage());
            throw new RuntimeException("Failed to delete item: " + e.getMessage(), e);
        }
    }

    private Item extractItemFromResultSet(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(String.valueOf(rs.getInt("item_id")));
        item.setItemName(rs.getString("item_name"));
        
        String conditionStr = rs.getString("item_condition");
        item.setCondition(EquipmentCondition.valueOf(conditionStr.toUpperCase().replace(" ", "_")));
        
        String statusStr = rs.getString("item_status");
        item.setStatus(ItemStatus.valueOf(statusStr.toUpperCase()));
        
        item.setEquipmentId(String.valueOf(rs.getInt("equipment_id")));
        return item;
    }
}
