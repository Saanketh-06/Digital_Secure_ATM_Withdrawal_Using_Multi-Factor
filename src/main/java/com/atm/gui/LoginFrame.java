package com.atm.gui;

import com.atm.auth.AuthenticationManager;
import com.atm.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Screen 1: Card Number + PIN (mandatory first authentication factor).
 */
public class LoginFrame extends JFrame {

    private final BankService bankService = new BankService();
    private final AuthenticationManager authManager = new AuthenticationManager(bankService);

    private JTextField cardNumberField;
    private JPasswordField pinField;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("Digital Secure ATM - Withdrawal System Using Multi-Factor Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIStyle.BG);
        setLayout(new GridBagLayout());

        add(buildCardPanel());
    }

    private JPanel buildCardPanel() {
        JPanel card = UIStyle.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(380, 460));

        JLabel bank = new JLabel("SECURE BANK ATM");
        bank.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bank.setForeground(UIStyle.PRIMARY);
        bank.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = UIStyle.titleLabel("Welcome");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UIStyle.subtitleLabel("Insert card details to begin");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cardLabel = new JLabel("Card Number");
        cardLabel.setFont(UIStyle.FONT_LABEL);
        cardLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardNumberField = new JTextField("1234567890123456");
        styleField(cardNumberField);

        JLabel pinLabel = new JLabel("PIN");
        pinLabel.setFont(UIStyle.FONT_LABEL);
        pinLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        pinField = new JPasswordField();
        styleField(pinField);

        JButton loginButton = UIStyle.primaryButton("Proceed");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.addActionListener(this::onLogin);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIStyle.FONT_SUBTITLE);
        statusLabel.setForeground(UIStyle.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel demoHint = new JLabel(
                "<html><center>Demo accounts:<br>1234567890123456 / PIN 1234<br>" +
                        "2345678901234567 / PIN 5678<br>3456789012345678 / PIN 0000</center></html>");
        demoHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoHint.setForeground(UIStyle.TEXT_MUTED);
        demoHint.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(bank);
        card.add(Box.createVerticalStrut(18));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(cardLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(cardNumberField);
        card.add(Box.createVerticalStrut(16));
        card.add(pinLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(pinField);
        card.add(Box.createVerticalStrut(22));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(18));
        card.add(demoHint);

        return card;
    }

    private void styleField(JTextField field) {
        field.setFont(UIStyle.FONT_LABEL);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD2E0), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }

    private void onLogin(ActionEvent e) {
        String cardNumber = cardNumberField.getText().trim();
        String pin = new String(pinField.getPassword());

        if (cardNumber.isEmpty() || pin.isEmpty()) {
            statusLabel.setText("Please enter card number and PIN.");
            return;
        }

        boolean valid = authManager.verifyPin(cardNumber, pin);
        if (!valid) {
            statusLabel.setText("Invalid card number or PIN. Try again.");
            pinField.setText("");
            return;
        }

        statusLabel.setForeground(UIStyle.SUCCESS);
        statusLabel.setText("PIN verified. Proceeding to second factor...");

        SecondFactorFrame secondFactorFrame =
                new SecondFactorFrame(authManager, bankService);
        secondFactorFrame.setVisible(true);
        dispose();
    }
}
