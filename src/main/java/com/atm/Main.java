package com.atm;

import com.atm.gui.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Entry point for the Digital Secure ATM Withdrawal System Using
 * Multi-Factor Authentication demo application.
 */
public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
