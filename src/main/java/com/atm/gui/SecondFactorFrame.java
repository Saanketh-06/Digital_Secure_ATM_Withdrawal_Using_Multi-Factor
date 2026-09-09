package com.atm.gui;

import com.atm.auth.AuthenticationManager;
import com.atm.model.Account;
import com.atm.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Screen 2: mandatory second authentication factor.
 * The user must pick exactly ONE of: Fingerprint, Face Recognition, or OTP,
 * and pass it before reaching the account dashboard.
 */
public class SecondFactorFrame extends JFrame {

    private final AuthenticationManager authManager;
    private final BankService bankService;
    private final Account account;

    private final CardLayout methodCardLayout = new CardLayout();
    private final JPanel methodPanel = new JPanel(methodCardLayout);

    private JLabel statusLabel;
    private String generatedOtp;

    public SecondFactorFrame(AuthenticationManager authManager, BankService bankService) {
        this.authManager = authManager;
        this.bankService = bankService;
        this.account = authManager.getPendingAccount();

        setTitle("Step 2 of 2 - Second Factor Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIStyle.BG);
        setLayout(new GridBagLayout());

        add(buildCardPanel());
    }

    private JPanel buildCardPanel() {
        JPanel card = UIStyle.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 520));

        JLabel title = UIStyle.titleLabel("Verify It's You");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = UIStyle.subtitleLabel(
                "Hi " + account.getAccountHolderName() + ", choose a second verification method");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel methodChooser = new JPanel(new GridLayout(1, 3, 8, 0));
        methodChooser.setOpaque(false);
        methodChooser.setAlignmentX(Component.CENTER_ALIGNMENT);
        methodChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton fingerprintTab = UIStyle.secondaryButton("Fingerprint");
        JButton faceTab = UIStyle.secondaryButton("Face ID");
        JButton otpTab = UIStyle.secondaryButton("OTP");

        fingerprintTab.addActionListener(e -> methodCardLayout.show(methodPanel, "FINGERPRINT"));
        faceTab.addActionListener(e -> methodCardLayout.show(methodPanel, "FACE"));
        otpTab.addActionListener(e -> {
            methodCardLayout.show(methodPanel, "OTP");
            sendOtp();
        });

        methodChooser.add(fingerprintTab);
        methodChooser.add(faceTab);
        methodChooser.add(otpTab);

        methodPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        methodPanel.setOpaque(false);
        methodPanel.add(buildFingerprintPanel(), "FINGERPRINT");
        methodPanel.add(buildFacePanel(), "FACE");
        methodPanel.add(buildOtpPanel(), "OTP");

        statusLabel = new JLabel(" ");
        statusLabel.setFont(UIStyle.FONT_SUBTITLE);
        statusLabel.setForeground(UIStyle.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));
        card.add(methodChooser);
        card.add(Box.createVerticalStrut(20));
        card.add(methodPanel);
        card.add(Box.createVerticalStrut(14));
        card.add(statusLabel);

        return card;
    }

    private JPanel buildFingerprintPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("🖐");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = UIStyle.subtitleLabel("Place your registered finger on the sensor");
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton scanButton = UIStyle.primaryButton("Simulate Fingerprint Scan");
        scanButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scanButton.addActionListener(e -> runScan(scanButton, true));

        panel.add(Box.createVerticalStrut(10));
        panel.add(icon);
        panel.add(Box.createVerticalStrut(8));
        panel.add(info);
        panel.add(Box.createVerticalStrut(16));
        panel.add(scanButton);
        return panel;
    }

    private JPanel buildFacePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("🙂");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = UIStyle.subtitleLabel("Look directly at the camera to verify your face");
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton scanButton = UIStyle.primaryButton("Simulate Face Scan");
        scanButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scanButton.addActionListener(e -> runScan(scanButton, false));

        panel.add(Box.createVerticalStrut(10));
        panel.add(icon);
        panel.add(Box.createVerticalStrut(8));
        panel.add(info);
        panel.add(Box.createVerticalStrut(16));
        panel.add(scanButton);
        return panel;
    }

    private JPanel buildOtpPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel info = UIStyle.subtitleLabel(
                "A 6-digit OTP has been sent to +91-XXXXX" +
                        account.getPhoneNumber().substring(account.getPhoneNumber().length() - 4));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField otpField = new JTextField();
        otpField.setFont(new Font("Consolas", Font.BOLD, 20));
        otpField.setHorizontalAlignment(JTextField.CENTER);
        otpField.setMaximumSize(new Dimension(200, 42));
        otpField.setAlignmentX(Component.CENTER_ALIGNMENT);
        otpField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBD2E0), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JButton resendButton = UIStyle.secondaryButton("Resend OTP");
        resendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resendButton.addActionListener(e -> sendOtp());

        JButton verifyButton = UIStyle.primaryButton("Verify OTP");
        verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verifyButton.addActionListener(e -> {
            boolean ok = authManager.verifyOtp(otpField.getText());
            if (ok) {
                proceedToDashboard();
            } else {
                statusLabel.setText("Incorrect or expired OTP. Please try again.");
            }
        });

        panel.add(Box.createVerticalStrut(10));
        panel.add(info);
        panel.add(Box.createVerticalStrut(14));
        panel.add(otpField);
        panel.add(Box.createVerticalStrut(14));
        panel.add(verifyButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(resendButton);
        return panel;
    }

    private void sendOtp() {
        generatedOtp = authManager.generateOtpForPendingAccount();
        // Simulated SMS gateway: shown in a dialog since this is a demo without real SMS delivery
        JOptionPane.showMessageDialog(this,
                "Simulated SMS to " + account.getPhoneNumber() + ":\n\nYour OTP is " + generatedOtp +
                        "\n(valid for 60 seconds)",
                "OTP Sent", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setForeground(UIStyle.DANGER);
        statusLabel.setText(" ");
    }

    private void runScan(JButton triggerButton, boolean isFingerprint) {
        triggerButton.setEnabled(false);
        statusLabel.setForeground(UIStyle.TEXT_MUTED);
        statusLabel.setText("Scanning, please hold still...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return isFingerprint ? authManager.performFingerprintScan()
                                      : authManager.performFaceScan();
            }

            @Override
            protected void done() {
                triggerButton.setEnabled(true);
                try {
                    boolean success = get();
                    if (success) {
                        proceedToDashboard();
                    } else {
                        statusLabel.setForeground(UIStyle.DANGER);
                        statusLabel.setText("Scan not recognized. Please try again.");
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(UIStyle.DANGER);
                    statusLabel.setText("Scan failed due to an error. Please retry.");
                }
            }
        };
        worker.execute();
    }

    private void proceedToDashboard() {
        DashboardFrame dashboard = new DashboardFrame(account, bankService, authManager);
        dashboard.setVisible(true);
        dispose();
    }
}
