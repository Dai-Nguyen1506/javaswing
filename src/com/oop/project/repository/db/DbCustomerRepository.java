package com.oop.project.repository.db;

import com.oop.project.model.Customer;
import com.oop.project.repository.CustomerRepository;
import com.oop.project.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database implementation of CustomerRepository using PostgreSQL
 */
public class DbCustomerRepository implements CustomerRepository {
    private final DatabaseConnection dbConnection;

    public DbCustomerRepository() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, cus_name, cus_email, cus_phone_number FROM customer ORDER BY customer_id";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all customers: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public Optional<Customer> findById(String customerId) {
        String sql = "SELECT customer_id, cus_name, cus_email, cus_phone_number FROM customer WHERE customer_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                return Optional.of(customer);
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error finding customer by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Customer> findByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, cus_name, cus_email, cus_phone_number FROM customer " +
                     "WHERE LOWER(cus_name) LIKE LOWER(?) ORDER BY cus_name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error finding customers by name: " + e.getMessage());
        }
        return customers;
    }

    public List<Customer> findByPhone(String phone) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, cus_name, cus_email, cus_phone_number FROM customer " +
                     "WHERE cus_phone_number LIKE ? ORDER BY cus_name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + phone + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error finding customers by phone: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public void save(Customer customer) {
        if (customer.getCustomerId() == null || customer.getCustomerId().isEmpty()) {
            insert(customer);
        } else {
            update(customer);
        }
    }

    private void insert(Customer customer) {
        String sql = "INSERT INTO customer (cus_name, cus_email, cus_phone_number) VALUES (?, ?, ?) RETURNING customer_id";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                customer.setCustomerId(String.valueOf(rs.getInt("customer_id")));
            }
        } catch (SQLException e) {
            System.err.println("Error inserting customer: " + e.getMessage());
            throw new RuntimeException("Failed to insert customer: " + e.getMessage(), e);
        }
    }

    private void update(Customer customer) {
        String sql = "UPDATE customer SET cus_name = ?, cus_email = ?, cus_phone_number = ? WHERE customer_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhone());
            pstmt.setInt(4, Integer.parseInt(customer.getCustomerId()));

            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            throw new RuntimeException("Failed to update customer: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String customerId) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(customerId));
            pstmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
        }
    }

    @Override
    public List<Customer> search(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, cus_name, cus_email, cus_phone_number FROM customer " +
                     "WHERE LOWER(cus_name) LIKE LOWER(?) OR cus_phone_number LIKE ? ORDER BY cus_name";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = extractCustomerFromResultSet(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
        return customers;
    }

    public void saveAll(List<Customer> customers) {
        for (Customer customer : customers) {
            save(customer);
        }
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(String.valueOf(rs.getInt("customer_id")));
        customer.setName(rs.getString("cus_name"));
        customer.setEmail(rs.getString("cus_email"));
        customer.setPhone(rs.getString("cus_phone_number"));
        customer.setAddress(""); // Address not in DB schema
        return customer;
    }
}
