package com.atm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bank account holder in the ATM system.
 * Holds the primary credential (card number + PIN) and secondary
 * authentication details (registered phone number for OTP).
 */
public class Account {

    private final String cardNumber;
    private final String pin;
    private final String accountHolderName;
    private final String phoneNumber;
    private double balance;
    private final List<Transaction> transactionHistory = new ArrayList<>();

    public Account(String cardNumber, String pin, String accountHolderName,
                    double balance, String phoneNumber) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.phoneNumber = phoneNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPin() {
        return pin;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    /** Masks the card number for display, e.g. **** **** **** 3456 */
    public String getMaskedCardNumber() {
        if (cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
