package com.oop.project.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class PlaceholderPanel extends JPanel {
    public PlaceholderPanel(String text) {
        super(new BorderLayout());
        add(new JLabel(text, JLabel.CENTER), BorderLayout.CENTER);
    }
}
