package com.oop.project.ui;

import com.oop.project.service.AuthService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);

    public LoginFrame(AuthService authService) {
        super("Sunshine Seashore - Login");
        this.authService = authService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonsPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JLabel hint = new JLabel("Default: admin/admin123 or staff/staff123");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(hint, gbc);

        return panel;
    }

    private JPanel buildButtonsPanel() {
        JPanel panel = new JPanel();
        JButton loginBtn = new JButton("Login");
        JButton exitBtn = new JButton("Exit");

        loginBtn.addActionListener(e -> attemptLogin());
        exitBtn.addActionListener(e -> System.exit(0));

        panel.add(loginBtn);
        panel.add(exitBtn);
        getRootPane().setDefaultButton(loginBtn);
        return panel;
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        var userOpt = authService.login(username, password);
        if (userOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Pass services from Main
        dispose();
    }
}
