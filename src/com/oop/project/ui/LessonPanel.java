package com.oop.project.ui;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.LessonPackage;
import com.oop.project.service.LessonPackageService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LessonPanel extends JPanel {
    private final LessonPackageService lessonService;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField packageIdField = new JTextField(15);
    private final JTextField nameField = new JTextField(20);
    private final JTextArea descriptionArea = new JTextArea(3, 20);
    private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 15, 480, 15));
    private final JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(50.0, 0.0, 1000.0, 5.0));
    private final JTextField instructorField = new JTextField(15);

    public LessonPanel(LessonPackageService lessonService) {
        this.lessonService = lessonService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"Package ID", "Name", "Description", "Duration (min)", "Price", "Instructor"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedPackage();
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
        panel.setBorder(BorderFactory.createTitledBorder("Lesson Package Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(panel, gbc, row++, "Package ID:", packageIdField);
        addFormField(panel, gbc, row++, "Name:", nameField);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        panel.add(descScroll, gbc);
        row++;

        addFormField(panel, gbc, row++, "Duration (min):", durationSpinner);
        addFormField(panel, gbc, row++, "Price ($):", priceSpinner);
        addFormField(panel, gbc, row++, "Instructor ID:", instructorField);

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
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(e -> addPackage());
        updateBtn.addActionListener(e -> updatePackage());
        deleteBtn.addActionListener(e -> deletePackage());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> refreshTable());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private void addPackage() {
        try {
            lessonService.createPackage(
                packageIdField.getText().trim(),
                nameField.getText().trim(),
                descriptionArea.getText().trim(),
                (Integer) durationSpinner.getValue(),
                ((Number) priceSpinner.getValue()).doubleValue(),
                instructorField.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Lesson package added successfully");
            clearForm();
            refreshTable();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePackage() {
        try {
            lessonService.updatePackage(
                packageIdField.getText().trim(),
                nameField.getText().trim(),
                descriptionArea.getText().trim(),
                (Integer) durationSpinner.getValue(),
                ((Number) priceSpinner.getValue()).doubleValue(),
                instructorField.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Lesson package updated successfully");
            refreshTable();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePackage() {
        String id = packageIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a package to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete package " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                lessonService.deletePackage(id);
                JOptionPane.showMessageDialog(this, "Lesson package deleted successfully");
                clearForm();
                refreshTable();
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTable() {
        List<LessonPackage> packages = lessonService.getAllPackages();
        tableModel.setRowCount(0);
        for (LessonPackage p : packages) {
            tableModel.addRow(new Object[]{
                p.getPackageId(),
                p.getName(),
                p.getDescription(),
                p.getDurationMinutes(),
                String.format("$%.2f", p.getPrice()),
                p.getInstructorId()
            });
        }
    }

    private void loadSelectedPackage() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            packageIdField.setText((String) tableModel.getValueAt(row, 0));
            nameField.setText((String) tableModel.getValueAt(row, 1));
            descriptionArea.setText((String) tableModel.getValueAt(row, 2));
            durationSpinner.setValue((Integer) tableModel.getValueAt(row, 3));
            String priceStr = (String) tableModel.getValueAt(row, 4);
            priceSpinner.setValue(Double.parseDouble(priceStr.replace("$", "")));
            instructorField.setText((String) tableModel.getValueAt(row, 5));
        }
    }

    private void clearForm() {
        packageIdField.setText("");
        nameField.setText("");
        descriptionArea.setText("");
        durationSpinner.setValue(30);
        priceSpinner.setValue(50.0);
        instructorField.setText("");
        table.clearSelection();
    }
}
