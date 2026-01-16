package com.oop.project.ui;

import com.oop.project.model.ContractStatus;
import com.oop.project.model.RentalContract;
import com.oop.project.service.CustomerService;
import com.oop.project.service.EquipmentService;
import com.oop.project.service.RentalContractService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final CustomerService customerService;
    private final EquipmentService equipmentService;
    private final RentalContractService rentalService;
    private final JLabel totalCustomersLabel = new JLabel("0");
    private final JLabel totalEquipmentLabel = new JLabel("0");
    private final JLabel activeRentalsLabel = new JLabel("0");
    private final JLabel overdueRentalsLabel = new JLabel("0");
    private final JLabel projectedRevenueLabel = new JLabel("$0.00");
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> sortCombo = new JComboBox<>(new String[]{"Contract Number", "Customer", "Price (High-Low)", "Duration (Long-Short)"});

    public DashboardPanel(CustomerService customerService, EquipmentService equipmentService, RentalContractService rentalService) {
        this.customerService = customerService;
        this.equipmentService = equipmentService;
        this.rentalService = rentalService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Statistics panel
        add(buildStatsPanel(), BorderLayout.NORTH);

        // Table
        String[] columns = {"Contract#", "Customer", "Equipment", "Duration", "Total Fee", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        // Control panel
        add(buildControlPanel(), BorderLayout.SOUTH);

        refreshDashboard();
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Summary Statistics"));

        panel.add(createStatCard("Total Customers", totalCustomersLabel, new Color(100, 149, 237)));
        panel.add(createStatCard("Total Equipment", totalEquipmentLabel, new Color(60, 179, 113)));
        panel.add(createStatCard("Active Rentals", activeRentalsLabel, new Color(255, 165, 0)));
        panel.add(createStatCard("Overdue Rentals", overdueRentalsLabel, new Color(220, 20, 60)));
        panel.add(createStatCard("Projected Revenue", projectedRevenueLabel, new Color(72, 209, 204)));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshDashboard());

        panel.add(new JLabel("Sort by:"));
        panel.add(sortCombo);
        JButton sortBtn = new JButton("Apply");
        sortBtn.addActionListener(e -> applySorting());
        panel.add(sortBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private void refreshDashboard() {
        // Update statistics
        totalCustomersLabel.setText(String.valueOf(customerService.getAllCustomers().size()));
        totalEquipmentLabel.setText(String.valueOf(equipmentService.getAllEquipment().size()));

        List<RentalContract> activeContracts = rentalService.getContractsByStatus(ContractStatus.ACTIVE);
        activeRentalsLabel.setText(String.valueOf(activeContracts.size()));

        List<RentalContract> overdueContracts = rentalService.getContractsByStatus(ContractStatus.OVERDUE);
        overdueRentalsLabel.setText(String.valueOf(overdueContracts.size()));

        double revenue = activeContracts.stream()
            .mapToDouble(RentalContract::getTotalFee)
            .sum();
        projectedRevenueLabel.setText(String.format("$%.2f", revenue));

        // Display all rentals
        displayRentals(rentalService.getAllContracts());
    }

    private void applySorting() {
        String sortBy = (String) sortCombo.getSelectedItem();
        List<RentalContract> contracts = rentalService.getAllContracts();

        switch (sortBy) {
            case "Contract Number":
                contracts.sort(Comparator.comparing(RentalContract::getContractNumber));
                break;
            case "Customer":
                contracts.sort(Comparator.comparing(RentalContract::getCustomerId));
                break;
            case "Price (High-Low)":
                contracts.sort(Comparator.comparingDouble(RentalContract::getTotalFee).reversed());
                break;
            case "Duration (Long-Short)":
                contracts.sort(Comparator.comparingInt(RentalContract::getDurationMinutes).reversed());
                break;
        }

        displayRentals(contracts);
    }

    private void displayRentals(List<RentalContract> contracts) {
        tableModel.setRowCount(0);
        for (RentalContract c : contracts) {
            tableModel.addRow(new Object[]{
                c.getContractNumber(),
                c.getCustomerId(),
                c.getEquipmentId(),
                c.getDurationMinutes() + " min",
                String.format("$%.2f", c.getTotalFee()),
                c.getStatus().getDisplayName()
            });
        }
    }
}
