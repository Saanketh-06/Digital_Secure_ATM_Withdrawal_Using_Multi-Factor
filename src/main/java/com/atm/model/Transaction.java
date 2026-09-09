package com.atm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single ATM transaction (balance inquiry, withdrawal, or deposit).
 */
public class Transaction {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s]  %-18s  Amount: Rs. %-10.2f  Balance After: Rs. %.2f",
                timestamp.format(FORMAT), type, amount, balanceAfter);
    }
}
