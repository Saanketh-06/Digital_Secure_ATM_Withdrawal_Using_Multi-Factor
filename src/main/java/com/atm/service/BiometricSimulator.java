package com.atm.service;

import java.security.SecureRandom;

/**
 * Simulates fingerprint and face-recognition hardware.
 * A real system would call a vendor SDK (e.g. a fingerprint sensor driver
 * or a face-recognition API) here. For the demo, a scan is simulated with
 * a short delay and a high success rate, with an occasional "no match"
 * outcome so the retry flow can be shown too.
 */
public class BiometricSimulator {

    private final SecureRandom random = new SecureRandom();

    /** Simulates scan hardware latency. Call this off the Swing Event Dispatch Thread. */
    public boolean simulateFingerprintScan() {
        sleep(1400);
        return random.nextInt(10) < 9; // ~90% success rate
    }

    public boolean simulateFaceRecognition() {
        sleep(1800);
        return random.nextInt(10) < 9; // ~90% success rate
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
