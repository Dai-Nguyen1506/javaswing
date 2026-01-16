package com.oop.project.ui;

import com.oop.project.exception.ValidationException;
import com.oop.project.model.ContractStatus;
import com.oop.project.model.RentalContract;
import com.oop.project.service.RentalContractService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RentalPanel extends JPanel {
    private final RentalContractService rentalService;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField contractNumberField = new JTextField(10);
    private final JTextField customerIdField = new JTextField(15);
    private final JTextField equipmentIdField = new JTextField(15);
    private final JTextField startTimeField = new JTextField(20);
    private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(60, 60, 7200, 30));
    private final JTextField lessonPackageField = new JTextField(15);
    private final JLabel rentalFeeLabel = new JLabel("$0.00");
    private final JLabel totalFeeLabel = new JLabel("$0.00");
    private final JComboBox<ContractStatus> statusCombo = new JComboBox<>(ContractStatus.values());
    private final JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{"All", "Draft", "Active", "Completed", "Returned", "Overdue"});

    public RentalPanel(RentalContractService rentalService) {
        this.rentalService = rentalService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columns = {"Contract#", "Customer", "Equipment", "Start Time", "Duration", "Rental Fee", "Lesson Fee", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedContract();
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // Form panel
        add(buildFormPanel(), BorderLayout.EAST);

        // Button panel
        add(buildButtonPanel(), BorderLayout.SOUTH);

        // Duration change listener for real-time pricing
        durationSpinner.addChangeListener(e -> updatePricing());

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Rental Contract Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        addFormField(panel, gbc, row++, "Contract Number:", contractNumberField);
        addFormField(panel, gbc, row++, "Customer ID:", customerIdField);
        addFormField(panel, gbc, row++, "Equipment ID:", equipmentIdField);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startTimeField.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timePanel.add(startTimeField);
        JButton nowBtn = new JButton("Now");
        nowBtn.addActionListener(e -> startTimeField.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        timePanel.add(nowBtn);
        panel.add(timePanel, gbc);
        row++;

        addFormField(panel, gbc, row++, "Duration (min):", durationSpinner);
        addFormField(panel, gbc, row++, "Lesson Package:", lessonPackageField);
        addFormField(panel, gbc, row++, "Rental Fee:", rentalFeeLabel);
        addFormField(panel, gbc, row++, "Total Fee:", totalFeeLabel);
        addFormField(panel, gbc, row++, "Status:", statusCombo);

        updatePricing();
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

        JButton createBtn = new JButton("Create");
        JButton updateStatusBtn = new JButton("Update Status");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton lookupBtn = new JButton("Lookup");
        JButton checkOverdueBtn = new JButton("Check Overdue");

        createBtn.addActionListener(e -> createContract());
        updateStatusBtn.addActionListener(e -> updateStatus());
        deleteBtn.addActionListener(e -> deleteContract());
        clearBtn.addActionListener(e -> clearForm());
        lookupBtn.addActionListener(e -> lookupContract());
        checkOverdueBtn.addActionListener(e -> checkOverdue());

        panel.add(createBtn);
        panel.add(updateStatusBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(lookupBtn);
        panel.add(checkOverdueBtn);
        panel.add(new JLabel("  Filter:"));
        panel.add(statusFilterCombo);
        JButton filterBtn = new JButton("Apply");
        filterBtn.addActionListener(e -> applyFilter());
        panel.add(filterBtn);

        return panel;
    }

    private void updatePricing() {
        int duration = (Integer) durationSpinner.getValue();
        double rentalFee = rentalService.calculateRentalFee(duration);
        rentalFeeLabel.setText(String.format("$%.2f", rentalFee));
        totalFeeLabel.setText(String.format("$%.2f", rentalFee));
    }

    private void createContract() {
        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeField.getText(), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            rentalService.createContract(
                contractNumberField.getText().trim().toUpperCase(),
                customerIdField.getText().trim(),
                equipmentIdField.getText().trim(),
                startTime,
                (Integer) durationSpinner.getValue(),
                lessonPackageField.getText().trim().isEmpty() ? null : lessonPackageField.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Rental contract created successfully");
            clearForm();
            refreshTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus() {
        String contractNumber = contractNumberField.getText().trim();
        if (contractNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a contract", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            rentalService.updateContractStatus(contractNumber, (ContractStatus) statusCombo.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Status updated successfully");
            refreshTable();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteContract() {
        String contractNumber = contractNumberField.getText().trim();
        if (contractNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a contract to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete contract " + contractNumber + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                rentalService.deleteContract(contractNumber);
                JOptionPane.showMessageDialog(this, "Contract deleted successfully");
                clearForm();
                refreshTable();
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lookupContract() {
        String contractNumber = JOptionPane.showInputDialog(this, "Enter Contract Number (e.g., A001):");
        if (contractNumber != null && !contractNumber.trim().isEmpty()) {
            var contractOpt = rentalService.getContractByNumber(contractNumber.trim().toUpperCase());
            if (contractOpt.isPresent()) {
                displayContract(contractOpt.get());
                JOptionPane.showMessageDialog(this, "Contract found via Random Access File lookup");
            } else {
                JOptionPane.showMessageDialog(this, "Contract not found", "Lookup Result", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void checkOverdue() {
        rentalService.checkAndUpdateOverdueContracts();
        JOptionPane.showMessageDialog(this, "Overdue contracts have been updated");
        refreshTable();
    }

    private void applyFilter() {
        String filter = (String) statusFilterCombo.getSelectedItem();
        if ("All".equals(filter)) {
            refreshTable();
        } else {
            ContractStatus status = ContractStatus.fromString(filter);
            List<RentalContract> contracts = rentalService.getContractsByStatus(status);
            displayContracts(contracts);
        }
    }

    private void refreshTable() {
        List<RentalContract> contracts = rentalService.getAllContracts();
        displayContracts(contracts);
    }

    private void displayContracts(List<RentalContract> contracts) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (RentalContract c : contracts) {
            tableModel.addRow(new Object[]{
                c.getContractNumber(),
                c.getCustomerId(),
                c.getEquipmentId(),
                c.getStartTime().format(formatter),
                c.getDurationMinutes() + " min",
                String.format("$%.2f", c.getRentalFee()),
                String.format("$%.2f", c.getLessonFee()),
                String.format("$%.2f", c.getTotalFee()),
                c.getStatus().getDisplayName()
            });
        }
    }

    private void loadSelectedContract() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String contractNumber = (String) tableModel.getValueAt(row, 0);
            var contractOpt = rentalService.getContractByNumber(contractNumber);
            contractOpt.ifPresent(this::displayContract);
        }
    }

    private void displayContract(RentalContract contract) {
        contractNumberField.setText(contract.getContractNumber());
        customerIdField.setText(contract.getCustomerId());
        equipmentIdField.setText(contract.getEquipmentId());
        startTimeField.setText(contract.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        durationSpinner.setValue(contract.getDurationMinutes());
        lessonPackageField.setText(contract.getLessonPackageId() != null ? contract.getLessonPackageId() : "");
        rentalFeeLabel.setText(String.format("$%.2f", contract.getRentalFee()));
        totalFeeLabel.setText(String.format("$%.2f", contract.getTotalFee()));
        statusCombo.setSelectedItem(contract.getStatus());
    }

    private void clearForm() {
        contractNumberField.setText("");
        customerIdField.setText("");
        equipmentIdField.setText("");
        startTimeField.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        durationSpinner.setValue(60);
        lessonPackageField.setText("");
        statusCombo.setSelectedItem(ContractStatus.DRAFT);
        updatePricing();
        table.clearSelection();
    }
}
