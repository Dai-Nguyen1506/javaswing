package com.oop.project.ui;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Equipment;
import com.oop.project.model.EquipmentCondition;
import com.oop.project.service.EquipmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EquipmentPanel extends JPanel {
    private final EquipmentService equipmentService;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(20);
    private final JTextField categoryField = new JTextField(15);
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
    private final JSpinner availableSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
    private final JComboBox<EquipmentCondition> conditionCombo = new JComboBox<>(EquipmentCondition.values());

    public EquipmentPanel(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"ID", "Name", "Category", "Quantity", "Available", "Condition"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedEquipment();
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
        panel.setBorder(BorderFactory.createTitledBorder("Equipment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(panel, gbc, row++, "Equipment ID:", idField);
        addFormField(panel, gbc, row++, "Name:", nameField);
        addFormField(panel, gbc, row++, "Category:", categoryField);
        addFormField(panel, gbc, row++, "Quantity:", quantitySpinner);
        addFormField(panel, gbc, row++, "Available:", availableSpinner);
        addFormField(panel, gbc, row++, "Condition:", conditionCombo);

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

        addBtn.addActionListener(e -> addEquipment());
        updateBtn.addActionListener(e -> updateEquipment());
        deleteBtn.addActionListener(e -> deleteEquipment());
        clearBtn.addActionListener(e -> clearForm());
        refreshBtn.addActionListener(e -> refreshTable());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private void addEquipment() {
        try {
            equipmentService.createEquipment(
                idField.getText().trim(),
                nameField.getText().trim(),
                categoryField.getText().trim(),
                (Integer) quantitySpinner.getValue(),
                (EquipmentCondition) conditionCombo.getSelectedItem()
            );
            JOptionPane.showMessageDialog(this, "Equipment added successfully");
            clearForm();
            refreshTable();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEquipment() {
        try {
            equipmentService.updateEquipment(
                idField.getText().trim(),
                nameField.getText().trim(),
                categoryField.getText().trim(),
                (Integer) quantitySpinner.getValue(),
                (Integer) availableSpinner.getValue(),
                (EquipmentCondition) conditionCombo.getSelectedItem()
            );
            JOptionPane.showMessageDialog(this, "Equipment updated successfully");
            refreshTable();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEquipment() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select equipment to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete equipment " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                equipmentService.deleteEquipment(id);
                JOptionPane.showMessageDialog(this, "Equipment deleted successfully");
                clearForm();
                refreshTable();
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTable() {
        List<Equipment> equipmentList = equipmentService.getAllEquipment();
        tableModel.setRowCount(0);
        for (Equipment e : equipmentList) {
            tableModel.addRow(new Object[]{
                e.getEquipmentId(),
                e.getName(),
                e.getCategory(),
                e.getQuantity(),
                e.getAvailable(),
                e.getCondition().getDisplayName()
            });
        }
    }

    private void loadSelectedEquipment() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idField.setText((String) tableModel.getValueAt(row, 0));
            nameField.setText((String) tableModel.getValueAt(row, 1));
            categoryField.setText((String) tableModel.getValueAt(row, 2));
            quantitySpinner.setValue((Integer) tableModel.getValueAt(row, 3));
            availableSpinner.setValue((Integer) tableModel.getValueAt(row, 4));
            String conditionStr = (String) tableModel.getValueAt(row, 5);
            conditionCombo.setSelectedItem(EquipmentCondition.fromString(conditionStr));
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        quantitySpinner.setValue(1);
        availableSpinner.setValue(1);
        conditionCombo.setSelectedIndex(0);
        table.clearSelection();
    }
}
