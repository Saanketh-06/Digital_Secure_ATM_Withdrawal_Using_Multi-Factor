package com.atm.service;

import java.security.SecureRandom;

/**
 * Simulates an OTP (One-Time Password) service.
 * In a real deployment this would call an SMS/Email gateway API;
 * for this demo the OTP is generated locally and shown to the user
 * in a "simulated SMS" pop-up so the flow can be showcased end-to-end.
 */
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final long VALID_DURATION_MS = 60_000; // OTP valid for 60 seconds

    private String currentOtp;
    private long generatedAtMillis;

    public String generateOtp() {
        int otp = 100000 + RANDOM.nextInt(900000); // 6-digit OTP
        currentOtp = String.valueOf(otp);
        generatedAtMillis = System.currentTimeMillis();
        return currentOtp;
    }

    public boolean verifyOtp(String inputOtp) {
        if (currentOtp == null || inputOtp == null) return false;
        boolean stillValid = (System.currentTimeMillis() - generatedAtMillis) <= VALID_DURATION_MS;
        return stillValid && currentOtp.equals(inputOtp.trim());
    }

    public long getRemainingSeconds() {
        long elapsed = System.currentTimeMillis() - generatedAtMillis;
        long remaining = (VALID_DURATION_MS - elapsed) / 1000;
        return Math.max(remaining, 0);
    }
}
