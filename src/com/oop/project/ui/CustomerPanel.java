package com.oop.project.ui;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Customer;
import com.oop.project.model.RentalContract;
import com.oop.project.repository.RentalContractRepository;
import com.oop.project.service.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CustomerPanel extends JPanel {
    private final CustomerService customerService;
    private final RentalContractRepository rentalContractRepository;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(20);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField emailField = new JTextField(20);
    private final JTextField searchField = new JTextField(20);

    public CustomerPanel(CustomerService customerService, RentalContractRepository rentalContractRepository) {
        this.customerService = customerService;
        this.rentalContractRepository = rentalContractRepository;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table - Removed Address column
        String[] columns = {"ID", "Name", "Phone", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedCustomer();
            }
        });
        
        // Add double-click listener to show rental history
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String customerId = table.getValueAt(row, 0).toString();
                        String customerName = table.getValueAt(row, 1).toString();
                        showRentalHistory(customerId, customerName);
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // Form panel
        add(buildFormPanel(), BorderLayout.EAST);

        // Button panel
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID (read-only)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        idField.setEditable(false);
        formPanel.add(idField, gbc);
        row++;

        // Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        row++;

        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        row++;

        // Info label
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>Double-click a customer to view rental history</i></html>");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC, 11f));
        formPanel.add(infoLabel, gbc);

        return formPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Search
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchCustomers());
        buttonPanel.add(searchBtn);

        buttonPanel.add(Box.createHorizontalStrut(20));

        // CRUD buttons
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> openAddDialog());
        buttonPanel.add(addBtn);

        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> updateCustomer());
        buttonPanel.add(updateBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteCustomer());
        buttonPanel.add(deleteBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        buttonPanel.add(clearBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        buttonPanel.add(refreshBtn);

        return buttonPanel;
    }

    private void showRentalHistory(String customerId, String customerName) {
        List<RentalContract> history = rentalContractRepository.findByCustomerId(customerId);
        
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                      "Rental History - " + customerName, true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        // Create table for rental history
        String[] columns = {"Contract ID", "Equipment ID", "Duration (min)", "Rental Fee", "Lesson Fee", "Total Fee", "Status"};
        DefaultTableModel historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (RentalContract contract : history) {
            historyModel.addRow(new Object[]{
                contract.getContractNumber(),
                contract.getEquipmentId(),
                contract.getDurationMinutes(),
                String.format("$%.2f", contract.getRentalFee()),
                String.format("$%.2f", contract.getLessonFee()),
                String.format("$%.2f", contract.getTotalFee()),
                contract.getStatus()
            });
        }

        JTable historyTable = new JTable(historyModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Total Rentals: " + history.size());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadSelectedCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        idField.setText(table.getValueAt(row, 0).toString());
        nameField.setText(table.getValueAt(row, 1).toString());
        phoneField.setText(table.getValueAt(row, 2).toString());
        emailField.setText(table.getValueAt(row, 3).toString());
    }
    
    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Customer", true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField newName = new JTextField(20);
        JTextField newPhone = new JTextField(20);
        JTextField newEmail = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(newName, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        panel.add(newPhone, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(newEmail, gbc);
        row++;

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String name = newName.getText().trim();
                String phone = newPhone.getText().trim();
                String email = newEmail.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name is required!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (phone.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Phone is required!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Customer customer = new Customer();
                customer.setName(name);
                customer.setPhone(phone);
                customer.setEmail(email);
                customer.setAddress("");

                customerService.addCustomer(customer);

                String customerId = customer.getCustomerId();
                JOptionPane.showMessageDialog(dialog,
                        "Customer added successfully!\nCustomer ID: " + customerId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                refreshTable();

                if (customerId != null) {
                    var opt = customerService.getCustomerById(customerId);
                    opt.ifPresent(CustomerPanel.this::displayCustomer);
                }
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addCustomer() {
        try {
            // Validate inputs
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (phoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Customer customer = new Customer();
            customer.setName(nameField.getText().trim());
            customer.setPhone(phoneField.getText().trim());
            customer.setEmail(emailField.getText().trim());
            customer.setAddress("");

            customerService.addCustomer(customer);
            
            // Show success with auto-generated ID
            String customerId = customer.getCustomerId();
            JOptionPane.showMessageDialog(this, 
                "Customer added successfully!\nCustomer ID: " + customerId,
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            refreshTable();
            
            // Load the newly added customer to show in form
            if (customerId != null) {
                var opt = customerService.getCustomerById(customerId);
                opt.ifPresent(this::displayCustomer);
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding customer: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Customer customer = new Customer();
            customer.setCustomerId(id);
            customer.setName(nameField.getText().trim());
            customer.setPhone(phoneField.getText().trim());
            customer.setEmail(emailField.getText().trim());
            customer.setAddress("");

            customerService.updateCustomer(customer);
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            refreshTable();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerService.deleteCustomer(id);
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete customer: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchCustomers() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        List<Customer> results = customerService.search(keyword);
        for (Customer c : results) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(),
                c.getName(),
                c.getPhone(),
                c.getEmail()
            });
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        searchField.setText("");
        table.clearSelection();
    }
    
    private void displayCustomer(Customer customer) {
        idField.setText(customer.getCustomerId());
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerService.findAll();
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(),
                c.getName(),
                c.getPhone(),
                c.getEmail()
            });
        }
    }
}
