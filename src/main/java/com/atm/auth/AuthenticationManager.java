package com.atm.auth;

import com.atm.model.Account;
import com.atm.service.BankService;
import com.atm.service.BiometricSimulator;
import com.atm.service.OtpService;

/**
 * Coordinates the two-step (multi-factor) authentication flow:
 *   Step 1 (mandatory): Card number + PIN
 *   Step 2 (mandatory, user picks ONE): Fingerprint OR Face Recognition OR OTP
 *
 * A session is only considered "authenticated" once BOTH steps succeed.
 */
public class AuthenticationManager {

    public enum Factor { FINGERPRINT, FACE, OTP }

    private final BankService bankService;
    private final OtpService otpService = new OtpService();
    private final BiometricSimulator biometricSimulator = new BiometricSimulator();

    private Account pendingAccount;   // passed PIN check, awaiting 2nd factor
    private boolean secondFactorPassed = false;

    public AuthenticationManager(BankService bankService) {
        this.bankService = bankService;
    }

    /** Step 1: verify card number + PIN. */
    public boolean verifyPin(String cardNumber, String pin) {
        Account account = bankService.authenticateWithPin(cardNumber, pin);
        if (account == null) {
            pendingAccount = null;
            return false;
        }
        pendingAccount = account;
        secondFactorPassed = false;
        return true;
    }

    public Account getPendingAccount() {
        return pendingAccount;
    }

    public String generateOtpForPendingAccount() {
        return otpService.generateOtp();
    }

    public boolean verifyOtp(String otp) {
        secondFactorPassed = otpService.verifyOtp(otp);
        return secondFactorPassed;
    }

    /** Runs on a background thread by the caller; blocks briefly to simulate hardware. */
    public boolean performFingerprintScan() {
        secondFactorPassed = biometricSimulator.simulateFingerprintScan();
        return secondFactorPassed;
    }

    public boolean performFaceScan() {
        secondFactorPassed = biometricSimulator.simulateFaceRecognition();
        return secondFactorPassed;
    }

    /** True only once PIN + chosen second factor have both succeeded. */
    public boolean isFullyAuthenticated() {
        return pendingAccount != null && secondFactorPassed;
    }

    public void reset() {
        pendingAccount = null;
        secondFactorPassed = false;
    }
}
