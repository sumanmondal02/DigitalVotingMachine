package utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;
import java.time.LocalDateTime;

public class VotingSecurityManager {  // Fixed: Renamed class to avoid conflict

    // Security constants
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD_HASH = hashPassword("admin123");
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    // Security patterns for validation
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern CANDIDATE_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_]{1,10}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]{2,50}$");
    private static final Pattern PARTY_PATTERN = Pattern.compile("^[A-Za-z0-9\\s]{2,30}$");

    // Security state tracking
    private Map<String, Integer> loginAttempts;
    private Map<String, LocalDateTime> lockedAccounts;
    private Set<String> activeSessions;
    private List<String> securityEvents;
    private SecureRandom secureRandom;

    // Data reference for validation
    private DataManager dataManager;

    /**
     * Constructor
     */
    public VotingSecurityManager() {
        this.loginAttempts = new HashMap<>();
        this.lockedAccounts = new HashMap<>();
        this.activeSessions = new HashSet<>();
        this.securityEvents = new ArrayList<>();
        this.secureRandom = new SecureRandom();

        System.out.println("VotingSecurityManager initialized with enhanced protection");
    }

    /**
     * Set data manager reference for validation
     */
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Authenticate admin user
     */
    public boolean authenticateAdmin(String username, String password) {
        try {
            // Check if account is locked
            if (isAccountLocked(username)) {
                logSecurityEvent("ADMIN_AUTH_BLOCKED", username, "Account locked due to excessive attempts");
                return false;
            }

            // Validate input
            if (!isValidInput(username) || !isValidInput(password)) {
                logSecurityEvent("ADMIN_AUTH_INVALID_INPUT", username, "Invalid input format");
                incrementLoginAttempts(username);
                return false;
            }

            // Check credentials
            boolean authenticated = ADMIN_USERNAME.equals(username) && 
                                  verifyPassword(password, ADMIN_PASSWORD_HASH);

            if (authenticated) {
                resetLoginAttempts(username);
                addActiveSession(username);
                logSecurityEvent("ADMIN_AUTH_SUCCESS", username, "Admin authentication successful");
                return true;
            } else {
                incrementLoginAttempts(username);
                logSecurityEvent("ADMIN_AUTH_FAILURE", username, "Invalid credentials provided");
                return false;
            }

        } catch (Exception e) {
            logSecurityEvent("ADMIN_AUTH_ERROR", username, "Authentication error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate voter with Aadhaar ID
     */
    public boolean authenticateVoter(String aadhaarID) {
        try {
            // Validate Aadhaar format
            if (!isValidAadhaarID(aadhaarID)) {
                logSecurityEvent("VOTER_AUTH_INVALID_FORMAT", aadhaarID, "Invalid Aadhaar ID format");
                return false;
            }

            // Check if voter is registered (requires DataManager)
            if (dataManager != null && !dataManager.isVoterRegistered(aadhaarID)) {
                logSecurityEvent("VOTER_AUTH_NOT_REGISTERED", aadhaarID, "Aadhaar ID not in voter database");
                return false;
            }

            // Check if voter has already voted
            if (dataManager != null && dataManager.hasVoterVoted(aadhaarID)) {
                logSecurityEvent("VOTER_AUTH_ALREADY_VOTED", aadhaarID, "Voter has already cast vote");
                return false;
            }

            // Check session status
            if (dataManager != null && !dataManager.isSessionActive()) {
                logSecurityEvent("VOTER_AUTH_SESSION_INACTIVE", aadhaarID, "Voting session not active");
                return false;
            }

            addActiveSession(aadhaarID);
            logSecurityEvent("VOTER_AUTH_SUCCESS", aadhaarID, "Voter authentication successful");
            return true;

        } catch (Exception e) {
            logSecurityEvent("VOTER_AUTH_ERROR", aadhaarID, "Authentication error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate Aadhaar ID format and security
     */
    public boolean isValidAadhaarID(String aadhaarID) {
        if (aadhaarID == null || aadhaarID.trim().isEmpty()) {
            return false;
        }

        // Check format (8 digits)
        if (!AADHAAR_PATTERN.matcher(aadhaarID).matches()) {
            return false;
        }

        // Additional security checks
        // Check for sequential numbers (e.g., 12345678)
        if (isSequential(aadhaarID)) {
            logSecurityEvent("AADHAAR_VALIDATION", aadhaarID, "Sequential number pattern detected");
            return false;
        }

        // Check for repeated digits (e.g., 11111111)
        if (isRepeatedDigits(aadhaarID)) {
            logSecurityEvent("AADHAAR_VALIDATION", aadhaarID, "Repeated digits pattern detected");
            return false;
        }

        return true;
    }

    /**
     * Check if string contains sequential numbers
     */
    private boolean isSequential(String str) {
        for (int i = 0; i < str.length() - 1; i++) {
            if (Character.getNumericValue(str.charAt(i + 1)) != 
                Character.getNumericValue(str.charAt(i)) + 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if string contains all repeated digits
     */
    private boolean isRepeatedDigits(String str) {
        char firstChar = str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) != firstChar) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate candidate information
     */
    public boolean isValidCandidateInfo(String candidateID, String name, String party) {
        return isValidCandidateID(candidateID) && 
               isValidName(name) && 
               isValidPartyName(party);
    }

    /**
     * Validate candidate ID
     */
    public boolean isValidCandidateID(String candidateID) {
        return candidateID != null && CANDIDATE_ID_PATTERN.matcher(candidateID).matches();
    }

    /**
     * Validate candidate name
     */
    public boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validate party name
     */
    public boolean isValidPartyName(String party) {
        return party != null && PARTY_PATTERN.matcher(party.trim()).matches();
    }

    /**
     * Sanitize input string
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }

        // Remove potentially dangerous characters
        String sanitized = input.replaceAll("[<>\"'&;]", "");


        // Trim whitespace
        sanitized = sanitized.trim();

        // Limit length
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }

        return sanitized;
    }

    /**
     * Check if input is valid (basic validation)
     */
    private boolean isValidInput(String input) {
        return input != null && 
               !input.trim().isEmpty() && 
               input.length() <= 100 &&
               !input.contains("<") && 
               !input.contains(">") &&
               !input.contains("\\") &&
               !input.contains("'") &&
               !input.contains("\"");
    }

    /**
     * Hash password for secure storage
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verify password against hash
     */
    private boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    /**
     * Check if account is locked
     */
    private boolean isAccountLocked(String username) {
        if (!lockedAccounts.containsKey(username)) {
            return false;
        }

        LocalDateTime lockTime = lockedAccounts.get(username);
        LocalDateTime unlockTime = lockTime.plusMinutes(LOCKOUT_DURATION_MINUTES);

        if (LocalDateTime.now().isAfter(unlockTime)) {
            lockedAccounts.remove(username);
            resetLoginAttempts(username);
            return false;
        }

        return true;
    }

    /**
     * Increment login attempts for user
     */
    private void incrementLoginAttempts(String username) {
        int attempts = loginAttempts.getOrDefault(username, 0) + 1;
        loginAttempts.put(username, attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            lockedAccounts.put(username, LocalDateTime.now());
            logSecurityEvent("ACCOUNT_LOCKED", username, 
                           "Account locked after " + attempts + " failed attempts");
        }
    }

    /**
     * Reset login attempts for user
     */
    private void resetLoginAttempts(String username) {
        loginAttempts.remove(username);
    }

    /**
     * Add active session
     */
    private void addActiveSession(String userID) {
        activeSessions.add(userID);
    }

    /**
     * Remove active session
     */
    public void removeActiveSession(String userID) {
        activeSessions.remove(userID);
        logSecurityEvent("SESSION_END", userID, "User session ended");
    }

    /**
     * Check if session is active
     */
    public boolean isSessionActive(String userID) {
        return activeSessions.contains(userID);
    }

    /**
     * Log security event
     */
    private void logSecurityEvent(String eventType, String userID, String description) {
        String timestamp = LocalDateTime.now().toString();
        String event = timestamp + " | " + eventType + " | " + userID + " | " + description;

        securityEvents.add(event);

        // Keep only recent events to prevent memory issues
        if (securityEvents.size() > 200) {
            securityEvents.remove(0);
        }

        // Also log to DataManager if available
        if (dataManager != null) {
            dataManager.logActivity("SECURITY_" + eventType, userID, description);
        }

        System.out.println("Security Event: " + event);
    }

    /**
     * Generate secure random token
     */
    public String generateSecureToken() {
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }

    /**
     * Encrypt sensitive data (simple encryption for demo)
     */
    public String encryptData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        // Simple XOR encryption for demo (not for production use)
        StringBuilder encrypted = new StringBuilder();
        String key = "VOTING_SYSTEM_KEY";

        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            char keyChar = key.charAt(i % key.length());
            encrypted.append((char)(c ^ keyChar));
        }

        return Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }

    /**
     * Decrypt sensitive data
     */
    public String decryptData(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }

        try {
            String data = new String(Base64.getDecoder().decode(encryptedData));
            StringBuilder decrypted = new StringBuilder();
            String key = "VOTING_SYSTEM_KEY";

            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                char keyChar = key.charAt(i % key.length());
                decrypted.append((char)(c ^ keyChar));
            }

            return decrypted.toString();

        } catch (Exception e) {
            logSecurityEvent("DECRYPT_ERROR", "SYSTEM", "Decryption failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Validate voting session integrity
     */
    public boolean validateSessionIntegrity() {
        try {
            if (dataManager == null) {
                return false;
            }

            // Check if data files exist and are readable
            File dataDir = new File("data");
            if (!dataDir.exists() || !dataDir.isDirectory()) {
                logSecurityEvent("INTEGRITY_CHECK", "SYSTEM", "Data directory missing or inaccessible");
                return false;
            }

            // Check critical files
            String[] criticalFiles = {"voters.txt", "candidates.txt", "votes.txt", "session.txt"};
            for (String filename : criticalFiles) {
                File file = new File("data" + File.separator + filename);
                if (!file.exists() || !file.canRead()) {
                    logSecurityEvent("INTEGRITY_CHECK", "SYSTEM", "Critical file missing or unreadable: " + filename);
                    return false;
                }
            }

            logSecurityEvent("INTEGRITY_CHECK", "SYSTEM", "Session integrity validation passed");
            return true;

        } catch (Exception e) {
            logSecurityEvent("INTEGRITY_CHECK", "SYSTEM", "Integrity check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get security events for monitoring
     */
    public List<String> getRecentSecurityEvents(int count) {
        int startIndex = Math.max(0, securityEvents.size() - count);
        return new ArrayList<>(securityEvents.subList(startIndex, securityEvents.size()));
    }

    /**
     * Check for suspicious activities
     */
    public List<String> checkForSuspiciousActivity() {
        List<String> suspiciousActivities = new ArrayList<>();

        // Check for multiple failed login attempts from same user
        for (Map.Entry<String, Integer> entry : loginAttempts.entrySet()) {
            if (entry.getValue() >= 2) {
                suspiciousActivities.add("Multiple failed login attempts from: " + entry.getKey());
            }
        }

        // Check for locked accounts
        for (Map.Entry<String, LocalDateTime> entry : lockedAccounts.entrySet()) {
            suspiciousActivities.add("Account locked: " + entry.getKey() + " at " + entry.getValue());
        }

        // Check for unusual session patterns
        if (activeSessions.size() > 10) {
            suspiciousActivities.add("Unusually high number of active sessions: " + activeSessions.size());
        }

        return suspiciousActivities;
    }

    /**
     * Generate security report
     */
    public String generateSecurityReport() {
        StringBuilder report = new StringBuilder();

        report.append("DIGITAL VOTING SYSTEM - SECURITY REPORT\n");
        report.append("======================================\n\n");
        report.append("Report Generated: ").append(LocalDateTime.now()).append("\n\n");

        report.append("ACTIVE SESSIONS:\n");
        report.append("- Total Active Sessions: ").append(activeSessions.size()).append("\n");
        for (String session : activeSessions) {
            report.append("  - ").append(session).append("\n");
        }
        report.append("\n");

        report.append("SECURITY EVENTS (Last 10):\n");
        List<String> recentEvents = getRecentSecurityEvents(10);
        for (String event : recentEvents) {
            report.append("- ").append(event).append("\n");
        }
        report.append("\n");

        report.append("SUSPICIOUS ACTIVITIES:\n");
        List<String> suspicious = checkForSuspiciousActivity();
        if (suspicious.isEmpty()) {
            report.append("- No suspicious activities detected\n");
        } else {
            for (String activity : suspicious) {
                report.append("- ").append(activity).append("\n");
            }
        }
        report.append("\n");

        report.append("SECURITY STATUS:\n");
        report.append("- System Integrity: ").append(validateSessionIntegrity() ? "PASS" : "FAIL").append("\n");
        report.append("- Failed Login Attempts: ").append(loginAttempts.size()).append("\n");
        report.append("- Locked Accounts: ").append(lockedAccounts.size()).append("\n");

        return report.toString();
    }

    /**
     * Clear all security state (for system reset)
     */
    public void clearSecurityState() {
        loginAttempts.clear();
        lockedAccounts.clear();
        activeSessions.clear();
        logSecurityEvent("SECURITY_RESET", "SYSTEM", "All security state cleared");
    }

    /**
     * Emergency security lockdown
     */
    public void emergencyLockdown() {
        activeSessions.clear();

        // Lock all accounts for extended period
        String[] accounts = {"admin"};
        for (String account : accounts) {
            lockedAccounts.put(account, LocalDateTime.now().plusHours(1));
        }

        logSecurityEvent("EMERGENCY_LOCKDOWN", "SYSTEM", "Emergency security lockdown activated");
        System.err.println("EMERGENCY LOCKDOWN ACTIVATED - All sessions terminated");
    }

    /**
     * Validate system security configuration
     */
    public boolean validateSecurityConfiguration() {
        List<String> issues = new ArrayList<>();

        // Check password strength (would be more sophisticated in production)
        if (ADMIN_PASSWORD_HASH.length() < 64) {
            issues.add("Weak password hash detected");
        }

        // Check file permissions (simplified check)
        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.canWrite()) {
            // This is expected, but in production you'd have more sophisticated checks
        }

        if (!issues.isEmpty()) {
            for (String issue : issues) {
                logSecurityEvent("CONFIG_ISSUE", "SYSTEM", issue);
            }
            return false;
        }

        logSecurityEvent("CONFIG_CHECK", "SYSTEM", "Security configuration validation passed");
        return true;
    }
}
