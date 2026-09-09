package com.atm.service;

import com.atm.model.Account;
import com.atm.model.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Core banking logic: account lookup, PIN check, balance / withdraw / deposit.
 * Uses an in-memory store seeded with demo accounts so the project can be
 * showcased without needing a real database.
 */
public class BankService {

    private final Map<String, Account> accounts = new HashMap<>();
    private static final double WITHDRAWAL_MULTIPLE = 100.0;
    private static final double MAX_WITHDRAWAL_PER_TXN = 50000.0;

    public BankService() {
        seedDemoAccounts();
    }

    private void seedDemoAccounts() {
        accounts.put("1234567890123456",
                new Account("1234567890123456", "1234", "Saanketh", 50000.00, "9876543210"));
        accounts.put("2345678901234567",
                new Account("2345678901234567", "5678", "Kiruthik", 125000.50, "9123456780"));
        accounts.put("3456789012345678",
                new Account("3456789012345678", "0000", "Demo User", 10000.00, "9999999999"));
    }

    /** Step 1 of authentication: card number + PIN. Returns null if invalid. */
    public Account authenticateWithPin(String cardNumber, String pin) {
        Account account = accounts.get(cardNumber.trim());
        if (account != null && account.getPin().equals(pin)) {
            return account;
        }
        return null;
    }

    public double checkBalance(Account account) {
        account.addTransaction(new Transaction("BALANCE INQUIRY", 0, account.getBalance()));
        return account.getBalance();
    }

    /** Result codes so the UI can show a precise reason for failure. */
    public enum WithdrawResult {
        SUCCESS, INVALID_AMOUNT, INSUFFICIENT_FUNDS, NOT_MULTIPLE_OF_HUNDRED, EXCEEDS_LIMIT
    }

    public WithdrawResult withdraw(Account account, double amount) {
        if (amount <= 0) return WithdrawResult.INVALID_AMOUNT;
        if (amount % WITHDRAWAL_MULTIPLE != 0) return WithdrawResult.NOT_MULTIPLE_OF_HUNDRED;
        if (amount > MAX_WITHDRAWAL_PER_TXN) return WithdrawResult.EXCEEDS_LIMIT;
        if (amount > account.getBalance()) return WithdrawResult.INSUFFICIENT_FUNDS;

        account.setBalance(account.getBalance() - amount);
        account.addTransaction(new Transaction("WITHDRAWAL", amount, account.getBalance()));
        return WithdrawResult.SUCCESS;
    }

    public boolean deposit(Account account, double amount) {
        if (amount <= 0) return false;
        account.setBalance(account.getBalance() + amount);
        account.addTransaction(new Transaction("DEPOSIT", amount, account.getBalance()));
        return true;
    }

    public double getMaxWithdrawalPerTxn() {
        return MAX_WITHDRAWAL_PER_TXN;
    }
}
