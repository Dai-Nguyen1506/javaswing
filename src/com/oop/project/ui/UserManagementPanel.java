package com.oop.project.ui;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Role;
import com.oop.project.model.User;
import com.oop.project.repository.UserRepository;
import com.oop.project.repository.db.DbUserRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin-only panel for viewing staff users
 */
public class UserManagementPanel extends JPanel {
    private final UserRepository userRepository;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField(20);

    public UserManagementPanel() {
        this.userRepository = new DbUserRepository();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"Username", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // Title and info panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("User Management - View Only");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        topPanel.add(titleLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Button panel
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Search
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchUsers());
        buttonPanel.add(searchBtn);

        buttonPanel.add(Box.createHorizontalStrut(20));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        buttonPanel.add(refreshBtn);

        return buttonPanel;
    }

    private void searchUsers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        List<User> users = userRepository.findAll();
        for (User u : users) {
            if (u.getUsername().toLowerCase().contains(keyword) || 
                u.getRole().toString().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[]{
                    u.getUsername(),
                    u.getRole()
                });
            }
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users = userRepository.findAll();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getUsername(),
                u.getRole()
            });
        }
    }
}
