package com.atm.gui;

import com.atm.auth.AuthenticationManager;
import com.atm.model.Account;
import com.atm.model.Transaction;
import com.atm.service.BankService;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Screen 3: main ATM dashboard, reachable only after both authentication
 * factors have succeeded. Offers Check Balance, Withdraw, Deposit, Mini
 * Statement, and Logout.
 */
public class DashboardFrame extends JFrame {

    private final Account account;
    private final BankService bankService;
    private final AuthenticationManager authManager;
    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    private JLabel balanceValueLabel;

    public DashboardFrame(Account account, BankService bankService, AuthenticationManager authManager) {
        this.account = account;
        this.bankService = bankService;
        this.authManager = authManager;

        setTitle("ATM Dashboard - " + account.getAccountHolderName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIStyle.BG);
        setLayout(new GridBagLayout());

        add(buildCardPanel());
    }

    private JPanel buildCardPanel() {
        JPanel card = UIStyle.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(440, 540));

        JLabel greeting = UIStyle.titleLabel("Welcome, " + account.getAccountHolderName());
        greeting.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cardNo = UIStyle.subtitleLabel(account.getMaskedCardNumber());
        cardNo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
        balancePanel.setBackground(new Color(0xEFF4FF));
        balancePanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        balancePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balancePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel balanceCaption = UIStyle.subtitleLabel("Available Balance");
        balanceCaption.setAlignmentX(Component.CENTER_ALIGNMENT);

        balanceValueLabel = new JLabel("Rs. " + money.format(account.getBalance()));
        balanceValueLabel.setFont(UIStyle.FONT_MONO_BALANCE);
        balanceValueLabel.setForeground(UIStyle.PRIMARY_DARK);
        balanceValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceValueLabel.setVisible(false); // hidden until "Check Balance" is pressed

        balancePanel.add(balanceCaption);
        balancePanel.add(Box.createVerticalStrut(6));
        balancePanel.add(balanceValueLabel);

        JButton checkBalanceButton = UIStyle.primaryButton("Check Balance");
        JButton withdrawButton = UIStyle.primaryButton("Withdraw Money");
        JButton depositButton = UIStyle.primaryButton("Deposit Money");
        JButton statementButton = UIStyle.secondaryButton("Mini Statement");
        JButton logoutButton = UIStyle.secondaryButton("Logout");

        for (JButton b : new JButton[]{checkBalanceButton, withdrawButton, depositButton,
                statementButton, logoutButton}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        }

        checkBalanceButton.addActionListener(e -> onCheckBalance());
        withdrawButton.addActionListener(e -> onWithdraw());
        depositButton.addActionListener(e -> onDeposit());
        statementButton.addActionListener(e -> onMiniStatement());
        logoutButton.addActionListener(e -> onLogout());

        card.add(greeting);
        card.add(Box.createVerticalStrut(4));
        card.add(cardNo);
        card.add(Box.createVerticalStrut(20));
        card.add(balancePanel);
        card.add(Box.createVerticalStrut(24));
        card.add(checkBalanceButton);
        card.add(Box.createVerticalStrut(10));
        card.add(withdrawButton);
        card.add(Box.createVerticalStrut(10));
        card.add(depositButton);
        card.add(Box.createVerticalStrut(10));
        card.add(statementButton);
        card.add(Box.createVerticalStrut(18));
        card.add(logoutButton);

        return card;
    }

    private void onCheckBalance() {
        double balance = bankService.checkBalance(account);
        balanceValueLabel.setText("Rs. " + money.format(balance));
        balanceValueLabel.setVisible(true);
    }

    private void onWithdraw() {
        String input = JOptionPane.showInputDialog(this,
                "Enter amount to withdraw (multiples of 100, max Rs. " +
                        money.format(bankService.getMaxWithdrawalPerTxn()) + "):",
                "Withdraw Money", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.isBlank()) return;

        double amount;
        try {
            amount = Double.parseDouble(input.trim());
        } catch (NumberFormatException ex) {
            showError("Please enter a valid numeric amount.");
            return;
        }

        BankService.WithdrawResult result = bankService.withdraw(account, amount);
        switch (result) {
            case SUCCESS -> {
                refreshBalanceIfVisible();
                JOptionPane.showMessageDialog(this,
                        "Please collect Rs. " + money.format(amount) + " from the dispenser.\n" +
                                "Remaining balance: Rs. " + money.format(account.getBalance()),
                        "Withdrawal Successful", JOptionPane.INFORMATION_MESSAGE);
            }
            case INSUFFICIENT_FUNDS -> showError("Insufficient balance for this withdrawal.");
            case NOT_MULTIPLE_OF_HUNDRED -> showError("Amount must be a multiple of Rs. 100.");
            case EXCEEDS_LIMIT -> showError("Amount exceeds the per-transaction limit of Rs. " +
                    money.format(bankService.getMaxWithdrawalPerTxn()) + ".");
            case INVALID_AMOUNT -> showError("Please enter an amount greater than zero.");
        }
    }

    private void onDeposit() {
        String input = JOptionPane.showInputDialog(this,
                "Enter amount to deposit:", "Deposit Money", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.isBlank()) return;

        double amount;
        try {
            amount = Double.parseDouble(input.trim());
        } catch (NumberFormatException ex) {
            showError("Please enter a valid numeric amount.");
            return;
        }

        boolean success = bankService.deposit(account, amount);
        if (success) {
            refreshBalanceIfVisible();
            JOptionPane.showMessageDialog(this,
                    "Rs. " + money.format(amount) + " deposited successfully.\n" +
                            "New balance: Rs. " + money.format(account.getBalance()),
                    "Deposit Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showError("Please enter an amount greater than zero.");
        }
    }

    private void onMiniStatement() {
        List<Transaction> history = account.getTransactionHistory();
        StringBuilder sb = new StringBuilder();
        if (history.isEmpty()) {
            sb.append("No transactions yet in this session.");
        } else {
            int start = Math.max(0, history.size() - 10);
            for (int i = start; i < history.size(); i++) {
                sb.append(history.get(i)).append("\n");
            }
        }
        JTextArea textArea = new JTextArea(sb.toString(), 12, 40);
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Mini Statement (last 10 transactions)", JOptionPane.PLAIN_MESSAGE);
    }

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            authManager.reset();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    private void refreshBalanceIfVisible() {
        if (balanceValueLabel.isVisible()) {
            balanceValueLabel.setText("Rs. " + money.format(account.getBalance()));
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Transaction Failed",
                JOptionPane.ERROR_MESSAGE);
    }
}
