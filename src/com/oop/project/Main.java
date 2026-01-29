package com.oop.project;

import com.oop.project.repository.*;
import com.oop.project.repository.file.*;
import com.oop.project.repository.db.*;
import com.oop.project.service.*;
import com.oop.project.ui.LoginFrame;
import com.oop.project.ui.MainFrame;
import com.oop.project.util.AppPaths;
import com.oop.project.util.DatabaseConnection;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {
    
    // Set to true to use PostgreSQL database, false to use file-based storage
    private static final boolean USE_DATABASE = true;

    public static void main(String[] args) {
        AppPaths.ensureDataDirectories();

        // Initialize repositories based on configuration
        UserRepository userRepository;
        CustomerRepository customerRepository;
        EquipmentRepository equipmentRepository;
        LessonPackageRepository lessonPackageRepository;
        RentalContractRepository rentalContractRepository;

        if (USE_DATABASE) {
            // Test database connection first
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (!dbConn.testConnection()) {
                System.err.println("Failed to connect to database. Please check your database configuration.");
                JOptionPane.showMessageDialog(null,
                    "Không thể kết nối đến database!\n" +
                    "Vui lòng kiểm tra:\n" +
                    "1. PostgreSQL đang chạy\n" +
                    "2. Database 'OOP Java' đã được tạo\n" +
                    "3. Thông tin kết nối trong database.properties",
                    "Lỗi Database",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            
            System.out.println("Using PostgreSQL database for storage");
            userRepository = new DbUserRepository();
            customerRepository = new DbCustomerRepository();
            equipmentRepository = new DbEquipmentRepository();
            lessonPackageRepository = new DbLessonPackageRepository();
            rentalContractRepository = new DbRentalContractRepository();
        } else {
            System.out.println("Using file-based storage");
            userRepository = new FileUserRepository();
            customerRepository = new FileCustomerRepository();
            equipmentRepository = new FileEquipmentRepository();
            lessonPackageRepository = new FileLessonPackageRepository();
            rentalContractRepository = new FileRentalContractRepository();
        }

        // Initialize services
        AuthService authService;
        if (USE_DATABASE) {
            // Use DbAuthService for database (supports plain text passwords)
            authService = new DbAuthService(userRepository);
        } else {
            // Use regular AuthService for file-based storage (hashed passwords)
            authService = new AuthService(userRepository);
        }
        authService.ensureDefaultUsersExist();

        CustomerService customerService = new CustomerService(customerRepository);
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);
        LessonPackageService lessonPackageService = new LessonPackageService(lessonPackageRepository);
        RentalContractService rentalContractService = new RentalContractService(
            rentalContractRepository, equipmentService, lessonPackageService
        );

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);

            // Show main frame after successful login
            loginFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    if (Session.currentUser().isPresent()) {
                        MainFrame mainFrame = new MainFrame(
                            authService, customerService, equipmentService,
                            lessonPackageService, rentalContractService
                        );
                        mainFrame.setVisible(true);
                    }
                }
            });
        });
    }
}

