package com.oop.project.ui;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Customer;
import com.oop.project.service.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private final CustomerService customerService;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(20);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField emailField = new JTextField(20);
    private final JTextArea addressArea = new JTextArea(3, 20);
    private final JTextField searchField = new JTextField(20);

    public CustomerPanel(CustomerService customerService) {
        this.customerService = customerService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};
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

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // Form panel
        add(buildFormPanel(), BorderLayout.EAST);

        // Button panel
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(panel, gbc, row++, "Customer ID:", idField);
        addFormField(panel, gbc, row++, "Name:", nameField);
        addFormField(panel, gbc, row++, "Phone:", phoneField);
        addFormField(panel, gbc, row++, "Email:", emailField);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        panel.add(addressScroll, gbc);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton searchBtn = new JButton("Search");

        addBtn.addActionListener(e -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        clearBtn.addActionListener(e -> clearForm());
        searchBtn.addActionListener(e -> searchCustomers());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(new JLabel("  Search:"));
        panel.add(searchField);
        panel.add(searchBtn);

        return panel;
    }

    private void addCustomer() {
        try {
            customerService.createCustomer(
                idField.getText().trim(),
                nameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressArea.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Customer added successfully");
            clearForm();
            refreshTable();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        try {
            customerService.updateCustomer(
                idField.getText().trim(),
                nameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressArea.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Customer updated successfully");
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

        int confirm = JOptionPane.showConfirmDialog(this, "Delete customer " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerService.deleteCustomer(id);
                JOptionPane.showMessageDialog(this, "Customer deleted successfully");
                clearForm();
                refreshTable();
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchCustomers() {
        String keyword = searchField.getText().trim();
        List<Customer> customers = customerService.searchCustomers(keyword);
        displayCustomers(customers);
    }

    private void refreshTable() {
        List<Customer> customers = customerService.getAllCustomers();
        displayCustomers(customers);
    }

    private void displayCustomers(List<Customer> customers) {
        tableModel.setRowCount(0);
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(),
                c.getName(),
                c.getPhone(),
                c.getEmail(),
                c.getAddress()
            });
        }
    }

    private void loadSelectedCustomer() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idField.setText((String) tableModel.getValueAt(row, 0));
            nameField.setText((String) tableModel.getValueAt(row, 1));
            phoneField.setText((String) tableModel.getValueAt(row, 2));
            emailField.setText((String) tableModel.getValueAt(row, 3));
            addressArea.setText((String) tableModel.getValueAt(row, 4));
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        table.clearSelection();
    }
}
