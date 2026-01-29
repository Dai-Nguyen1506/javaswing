package com.oop.project.ui;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.Equipment;
import com.oop.project.model.Item;
import com.oop.project.repository.db.DbItemRepository;
import com.oop.project.service.EquipmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EquipmentPanel extends JPanel {
    private final EquipmentService equipmentService;
    private final DbItemRepository itemRepository;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(20);
    private final JTextField feeField = new JTextField(10);
    private final JTextField quantityField = new JTextField(10);
    private final JTextField availableField = new JTextField(10);
    private final JTextField searchField = new JTextField(20);

    public EquipmentPanel(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
        this.itemRepository = new DbItemRepository();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table - Removed Category and Condition, Added Fee
        String[] columns = {"ID", "Name", "Fee ($)", "Quantity", "Available"};
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
        
        // Add double-click listener to show items
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String equipmentId = table.getValueAt(row, 0).toString();
                        String equipmentName = table.getValueAt(row, 1).toString();
                        showItemsDialog(equipmentId, equipmentName);
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Equipment Details"));
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

        // Fee
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Fee ($/hr):*"), gbc);
        gbc.gridx = 1;
        formPanel.add(feeField, gbc);
        row++;

        // Quantity (read-only - managed through items)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        quantityField.setEditable(false);
        quantityField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(quantityField, gbc);
        row++;

        // Available (read-only - managed through items)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Available:"), gbc);
        gbc.gridx = 1;
        availableField.setEditable(false);
        availableField.setBackground(Color.LIGHT_GRAY);
        formPanel.add(availableField, gbc);
        row++;

        // Info label
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>Double-click equipment to view items</i></html>");
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
        searchBtn.addActionListener(e -> searchEquipment());
        buttonPanel.add(searchBtn);

        buttonPanel.add(Box.createHorizontalStrut(20));

        // CRUD buttons
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> openAddDialog());
        buttonPanel.add(addBtn);

        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(e -> updateEquipment());
        buttonPanel.add(updateBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteEquipment());
        buttonPanel.add(deleteBtn);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearForm());
        buttonPanel.add(clearBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        buttonPanel.add(refreshBtn);

        return buttonPanel;
    }

    private void showItemsDialog(String equipmentId, String equipmentName) {
        List<Item> items = itemRepository.findByEquipmentId(equipmentId);
        
        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                      "Items - " + equipmentName, true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);

        // Create table for items
        String[] columns = {"Item ID", "Item Name", "Condition", "Status"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Item item : items) {
            itemsModel.addRow(new Object[]{
                item.getItemId(),
                item.getItemName(),
                item.getCondition(),
                item.getStatus()
            });
        }

        JTable itemsTable = new JTable(itemsModel);
        
        // Add double-click to edit item
        itemsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = itemsTable.getSelectedRow();
                    if (row >= 0) {
                        String itemId = itemsTable.getValueAt(row, 0).toString();
                        String itemName = itemsTable.getValueAt(row, 1).toString();
                        String condition = itemsTable.getValueAt(row, 2).toString();
                        String status = itemsTable.getValueAt(row, 3).toString();
                        openEditItemDialog(itemId, itemName, condition, status, equipmentId, itemsModel, row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(itemsTable);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Total Items: " + items.size() + " (Double-click to edit)");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton addItemBtn = new JButton("Add Item");
        addItemBtn.addActionListener(e -> {
            openAddItemDialog(equipmentId, equipmentName, itemsModel, dialog);
        });
        btnPanel.add(addItemBtn);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> {
            dialog.dispose();
            refreshTable(); // Refresh main table to update quantities
        });
        btnPanel.add(closeBtn);
        
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void openAddItemDialog(String equipmentId, String equipmentName, DefaultTableModel itemsModel, JDialog parentDialog) {
        JDialog dialog = new JDialog(parentDialog, "Add Item to " + equipmentName, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parentDialog);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField itemNameField = new JTextField(20);
        JComboBox<String> conditionCombo = new JComboBox<>(new String[]{"Good", "Damaged", "Under Repair"});

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Item Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(itemNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Condition:*"), gbc);
        gbc.gridx = 1;
        panel.add(conditionCombo, gbc);
        row++;

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String itemName = itemNameField.getText().trim();
                if (itemName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Item name is required!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Item newItem = new Item();
                newItem.setItemName(itemName);
                newItem.setEquipmentId(equipmentId);
                newItem.setCondition(com.oop.project.model.EquipmentCondition.valueOf(
                    conditionCombo.getSelectedItem().toString().toUpperCase().replace(" ", "_")));
                newItem.setStatus(com.oop.project.model.ItemStatus.AVAILABLE);

                itemRepository.save(newItem);

                // Refresh items table
                itemsModel.addRow(new Object[]{
                    newItem.getItemId(),
                    newItem.getItemName(),
                    newItem.getCondition(),
                    newItem.getStatus()
                });

                JOptionPane.showMessageDialog(dialog, "Item added successfully!");
                dialog.dispose();
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
    
    private void openEditItemDialog(String itemId, String itemName, String condition, String status,
                                    String equipmentId, DefaultTableModel itemsModel, int tableRow) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Item", true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(itemName, 20);
        JComboBox<String> conditionCombo = new JComboBox<>(new String[]{"Good", "Damaged", "Under Repair"});
        conditionCombo.setSelectedItem(condition);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Unavailable"});
        statusCombo.setSelectedItem(status);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Item ID:"), gbc);
        gbc.gridx = 1;
        JTextField idField = new JTextField(itemId, 20);
        idField.setEditable(false);
        panel.add(idField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Item Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Condition:*"), gbc);
        gbc.gridx = 1;
        panel.add(conditionCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Status:*"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);
        row++;

        JButton saveBtn = new JButton("Save");
        JButton deleteBtn = new JButton("Delete");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                Item item = itemRepository.findById(itemId).orElse(null);
                if (item == null) {
                    JOptionPane.showMessageDialog(dialog, "Item not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                item.setItemName(nameField.getText().trim());
                item.setCondition(com.oop.project.model.EquipmentCondition.valueOf(
                    conditionCombo.getSelectedItem().toString().toUpperCase().replace(" ", "_")));
                item.setStatus(com.oop.project.model.ItemStatus.valueOf(
                    statusCombo.getSelectedItem().toString().toUpperCase()));

                itemRepository.save(item);

                // Update table row
                itemsModel.setValueAt(item.getItemName(), tableRow, 1);
                itemsModel.setValueAt(item.getCondition(), tableRow, 2);
                itemsModel.setValueAt(item.getStatus(), tableRow, 3);

                JOptionPane.showMessageDialog(dialog, "Item updated successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to delete this item?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    itemRepository.delete(itemId);
                    itemsModel.removeRow(tableRow);
                    JOptionPane.showMessageDialog(dialog, "Item deleted successfully!");
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(saveBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadSelectedEquipment() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        idField.setText(table.getValueAt(row, 0).toString());
        nameField.setText(table.getValueAt(row, 1).toString());
        feeField.setText(table.getValueAt(row, 2).toString().replace("$", ""));
        quantityField.setText(table.getValueAt(row, 3).toString());
        availableField.setText(table.getValueAt(row, 4).toString());
    }
    
    private void openAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Equipment", true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField newName = new JTextField(20);
        JTextField newFee = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Equipment Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(newName, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Fee ($/hr):*"), gbc);
        gbc.gridx = 1;
        panel.add(newFee, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel note = new JLabel("<html><i>Note: Quantity will be 0. Add items to increase quantity.</i></html>");
        note.setFont(note.getFont().deriveFont(Font.ITALIC, 11f));
        panel.add(note, gbc);
        row++;

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String name = newName.getText().trim();
                String feeStr = newFee.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Equipment name is required!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (feeStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Fee is required!",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Equipment equipment = new Equipment();
                equipment.setName(name);
                equipment.setFee(Double.parseDouble(feeStr));
                equipment.setQuantity(0);
                equipment.setAvailable(0);
                equipment.setCategory(name);
                equipment.setCondition(com.oop.project.model.EquipmentCondition.GOOD);

                equipmentService.addEquipment(equipment);

                String equipmentId = equipment.getEquipmentId();
                JOptionPane.showMessageDialog(dialog,
                        "Equipment added successfully!\nEquipment ID: " + equipmentId + "\nClick on it to add items.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                refreshTable();

                if (equipmentId != null) {
                    var opt = equipmentService.getEquipmentById(equipmentId);
                    opt.ifPresent(EquipmentPanel.this::displayEquipment);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid number for fee",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
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

    private void addEquipment() {
        try {
            // Validate inputs
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Equipment name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (feeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fee is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (quantityField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Quantity is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (availableField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Available is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Equipment equipment = new Equipment();
            equipment.setName(nameField.getText().trim());
            equipment.setFee(Double.parseDouble(feeField.getText().trim()));
            equipment.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            equipment.setAvailable(Integer.parseInt(availableField.getText().trim()));
            equipment.setCategory(nameField.getText().trim());
            equipment.setCondition(com.oop.project.model.EquipmentCondition.GOOD);

            equipmentService.addEquipment(equipment);
            
            // Show success with auto-generated ID
            String equipmentId = equipment.getEquipmentId();
            JOptionPane.showMessageDialog(this, 
                "Equipment added successfully!\nEquipment ID: " + equipmentId,
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            refreshTable();
            
            // Load the newly added equipment to show in form
            if (equipmentId != null) {
                var opt = equipmentService.getEquipmentById(equipmentId);
                opt.ifPresent(this::displayEquipment);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for fee, quantity, and available",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding equipment: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEquipment() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select equipment to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Equipment equipment = new Equipment();
            equipment.setEquipmentId(id);
            equipment.setName(nameField.getText().trim());
            equipment.setFee(Double.parseDouble(feeField.getText().trim()));
            equipment.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            equipment.setAvailable(Integer.parseInt(availableField.getText().trim()));
            equipment.setCategory(nameField.getText().trim());

            equipmentService.updateEquipment(equipment);
            JOptionPane.showMessageDialog(this, "Equipment updated successfully!");
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for fee, quantity, and available",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
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

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this equipment?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                equipmentService.deleteEquipment(id);
                JOptionPane.showMessageDialog(this, "Equipment deleted successfully!");
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete equipment: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchEquipment() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }

        tableModel.setRowCount(0);
        List<Equipment> results = equipmentService.findByCategory(keyword);
        for (Equipment eq : results) {
            tableModel.addRow(new Object[]{
                eq.getEquipmentId(),
                eq.getName(),
                String.format("$%.2f", eq.getFee()),
                eq.getQuantity(),
                eq.getAvailable()
            });
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        feeField.setText("");
        quantityField.setText("");
        availableField.setText("");
        searchField.setText("");
        table.clearSelection();
    }
    
    private void displayEquipment(Equipment equipment) {
        idField.setText(equipment.getEquipmentId());
        nameField.setText(equipment.getName());
        feeField.setText(String.format("%.2f", equipment.getFee()));
        quantityField.setText(String.valueOf(equipment.getQuantity()));
        availableField.setText(String.valueOf(equipment.getAvailable()));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Equipment> equipmentList = equipmentService.findAll();
        for (Equipment eq : equipmentList) {
            tableModel.addRow(new Object[]{
                eq.getEquipmentId(),
                eq.getName(),
                String.format("$%.2f", eq.getFee()),
                eq.getQuantity(),
                eq.getAvailable()
            });
        }
    }
}
