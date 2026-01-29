package com.oop.project.ui;

import com.oop.project.model.Role;
import com.oop.project.model.User;
import com.oop.project.repository.db.DbRentalContractRepository;
import com.oop.project.service.AuthService;
import com.oop.project.service.CustomerService;
import com.oop.project.service.EquipmentService;
import com.oop.project.service.LessonPackageService;
import com.oop.project.service.RentalContractService;
import com.oop.project.service.Session;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private final AuthService authService;
    private final CustomerService customerService;
    private final EquipmentService equipmentService;
    private final LessonPackageService lessonPackageService;
    private final RentalContractService rentalContractService;

    public MainFrame(AuthService authService, CustomerService customerService, 
                     EquipmentService equipmentService, LessonPackageService lessonPackageService,
                     RentalContractService rentalContractService) {
        super("Sunshine Seashore - Rental & Lesson System");
        this.authService = authService;
        this.customerService = customerService;
        this.equipmentService = equipmentService;
        this.lessonPackageService = lessonPackageService;
        this.rentalContractService = rentalContractService;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildTabs(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                authService.logout();
                System.exit(0);
            }
        });
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        
        // Get current user role
        User currentUser = Session.currentUser().orElse(null);
        Role userRole = currentUser != null ? currentUser.getRole() : Role.STAFF;

        // Admin-only: User Management tab
        if (userRole == Role.ADMIN) {
            tabs.addTab("User Management", new UserManagementPanel());
        }

        // Both Admin and Staff can manage customers, equipment, rentals
        tabs.addTab("Customers", new CustomerPanel(customerService, new DbRentalContractRepository()));
        tabs.addTab("Equipment", new EquipmentPanel(equipmentService));
        tabs.addTab("Rentals", new RentalPanel(rentalContractService));
        tabs.addTab("Dashboard", new DashboardPanel(customerService, equipmentService, rentalContractService));

        return tabs;
    }
}
