package com.oop.project;

import com.oop.project.repository.file.*;
import com.oop.project.service.*;
import com.oop.project.ui.LoginFrame;
import com.oop.project.ui.MainFrame;
import com.oop.project.util.AppPaths;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        AppPaths.ensureDataDirectories();

        // Initialize repositories
        FileUserRepository userRepository = new FileUserRepository();
        FileCustomerRepository customerRepository = new FileCustomerRepository();
        FileEquipmentRepository equipmentRepository = new FileEquipmentRepository();
        FileLessonPackageRepository lessonPackageRepository = new FileLessonPackageRepository();
        FileRentalContractRepository rentalContractRepository = new FileRentalContractRepository();

        // Initialize services
        AuthService authService = new AuthService(userRepository);
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

