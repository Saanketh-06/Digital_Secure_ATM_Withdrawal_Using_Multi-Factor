# Digital Secure ATM Withdrawal System Using Multi-Factor Authentication

A desktop Java demo of a secure ATM system. Every session requires **mandatory
PIN verification**, followed by a **mandatory second authentication factor**
(Fingerprint, Face Recognition, or OTP — user's choice). Only after both
factors succeed can the user Check Balance, Withdraw Money, or Deposit Money.

Built to be shown as a project demo / viva: it runs standalone, needs no
external database or hardware, and every "biometric" or "SMS OTP" step is
clearly simulated for demonstration purposes while following the same
authentication flow a real ATM would use.

---

## Authentication Flow

```
 ┌───────────────┐      ┌───────────────────────────┐      ┌────────────────┐
 │  Step 1        │      │  Step 2 (choose ONE)        │      │  Step 3         │
 │  Card + PIN    │ ───► │  Fingerprint / Face / OTP   │ ───► │  ATM Dashboard  │
 └───────────────┘      └───────────────────────────┘      └────────────────┘
```

* **Step 1 – PIN Verification (mandatory):** card number + 4-digit PIN checked
  against the account store.
* **Step 2 – Second Factor (mandatory, any ONE):**
  * **Fingerprint** – simulated sensor scan (~1.5s) with success/failure outcome.
  * **Face Recognition** – simulated camera scan (~1.8s) with success/failure outcome.
  * **OTP** – a 6-digit one-time code is generated and shown in a "simulated SMS"
    pop-up (valid for 60 seconds), which the user must enter correctly.
* **Step 3 – Dashboard:** only reachable once *both* factors pass.
  * Check Balance
  * Withdraw Money (multiples of ₹100, capped per transaction, blocked if
    insufficient funds)
  * Deposit Money
  * Mini Statement (last 10 transactions of the session)
  * Logout

---

## Tech Stack

| Layer            | Technology                                            |
|-------------------|-------------------------------------------------------|
| Language           | Java 17                                               |
| UI / Frontend      | Java Swing + [FlatLaf](https://www.formdev.com/flatlaf/) (modern flat look-and-feel) |
| Build tool         | Maven                                                 |
| Data store         | In-memory demo accounts (`HashMap`) — easy to swap for JDBC/MySQL later |

---

## Project Structure

```
digital-secure-atm/
├── pom.xml
├── README.md
└── src/main/java/com/atm/
    ├── Main.java                       # application entry point
    ├── model/
    │   ├── Account.java                # account + demo data holder
    │   └── Transaction.java            # single transaction record
    ├── service/
    │   ├── BankService.java            # balance/withdraw/deposit business rules
    │   ├── OtpService.java             # OTP generation & verification (simulated SMS)
    │   └── BiometricSimulator.java     # fingerprint/face scan simulation
    ├── auth/
    │   └── AuthenticationManager.java  # orchestrates PIN + 2nd-factor session state
    └── gui/
        ├── LoginFrame.java             # Step 1 screen
        ├── SecondFactorFrame.java      # Step 2 screen
        ├── DashboardFrame.java         # Step 3 screen
        └── UIStyle.java                # shared fonts/colors/button styles
```

---

## Demo Accounts

| Card Number         | PIN  | Name          | Starting Balance |
|----------------------|------|---------------|-------------------|
| 1234567890123456     | 1234 | Saanketh      | ₹50,000.00        |
| 2345678901234567     | 5678 | Kiruthik      | ₹1,25,000.50      |
| 3456789012345678     | 0000 | Demo User     | ₹10,000.00        |

---

## Run It in VS Code

1. Install the **Extension Pack for Java** and **Maven for Java** extensions in VS Code.
2. Open this folder (`digital-secure-atm`) in VS Code.
3. Let Maven download dependencies (it will fetch FlatLaf automatically).
4. Open `src/main/java/com/atm/Main.java` and click **Run** above `main()`,
   *or* use the terminal:

   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.atm.Main"
   ```

   (If you don't have the `exec` plugin configured, simply run the compiled
   class from VS Code's Run button — it works out of the box with the Java
   extension.)

### Build a runnable JAR

```bash
mvn clean package
java -jar target/digital-secure-atm.jar
```

## Notes for the Demo / Viva

* This is a **standalone desktop demo** — fingerprint/face scanning and OTP
  SMS delivery are simulated in software (clearly labelled as such in the UI
  and code comments) since no real biometric hardware or SMS gateway is
  wired in. The authentication *flow*, *state management*, and *business
  rules* (mandatory two-factor gate, withdrawal limits, insufficient-funds
  checks) are fully real and enforced in code.
* To extend this into a production-style project: swap the in-memory
  `HashMap` in `BankService` for a JDBC-backed repository (MySQL/PostgreSQL),
  replace `BiometricSimulator` with a real fingerprint SDK / face-recognition
  API, and replace `OtpService`'s local generation with a real SMS/Email
  gateway (e.g., Twilio).

## Possible Future Enhancements

- Persist accounts and transaction history to a real database
- Add account creation / admin panel
- PIN lockout after repeated failed attempts
- Printable transaction receipts
- Multi-language support
