package utils;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataManager {

    // File paths for data storage
    private static final String DATA_DIR = "data";
    private static final String VOTERS_FILE = DATA_DIR + File.separator + "voters.txt";
    private static final String CANDIDATES_FILE = DATA_DIR + File.separator + "candidates.txt";
    private static final String VOTES_FILE = DATA_DIR + File.separator + "votes.txt";
    private static final String SESSION_FILE = DATA_DIR + File.separator + "session.txt";
    private static final String ACTIVITY_LOG = DATA_DIR + File.separator + "activity.log";
    private static final String ADMIN_FILE = DATA_DIR + File.separator + "admin.txt";

    // In-memory data structures for quick access
    private Set<String> registeredVoters;
    private Set<String> votedVoters;
    private Map<String, String[]> candidates; // ID -> [Name, Party]
    private Map<String, Integer> voteCount;
    private List<String> activityLog;
    private boolean sessionActive;

    // Security and validation
    private static final int MAX_VOTERS = 20;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    /**
     * Constructor - Initialize DataManager
     */
    public DataManager() {
        registeredVoters = new HashSet<>();
        votedVoters = new HashSet<>();
        candidates = new HashMap<>();
        voteCount = new HashMap<>();
        activityLog = new ArrayList<>();
        sessionActive = false;

        System.out.println("DataManager initialized");
    }

    /**
     * Initialize all data files and create default data if needed
     */
    public void initializeDataFiles() throws IOException {
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
            System.out.println("Created data directory: " + DATA_DIR);
        }

        // Initialize voter database with 20 pre-registered voters
        initializeVotersFile();

        // Initialize other files
        initializeAdminFile();
        initializeCandidatesFile();
        initializeVotesFile();
        initializeSessionFile();
        initializeActivityLog();

        // Load existing data
        loadSystemData();

        logActivity("SYSTEM_INIT", "SYSTEM", "DataManager initialized successfully");
    }

    /**
     * Initialize voters file with 20 pre-registered 8-digit Aadhaar IDs
     */
    private void initializeVotersFile() throws IOException {
        File votersFile = new File(VOTERS_FILE);

        if (!votersFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(votersFile))) {
                writer.println("# Registered Voters Database");
                writer.println("# Format: AadhaarID");
                writer.println("# Each ID is 8 digits, pre-registered for voting");
                writer.println();

                // Generate 20 unique 8-digit Aadhaar IDs
                Set<String> uniqueIds = new HashSet<>();
                Random random = new Random(12345); // Fixed seed for consistency

                while (uniqueIds.size() < MAX_VOTERS) {
                    int aadhaarId = 10000000 + random.nextInt(90000000);
                    uniqueIds.add(String.valueOf(aadhaarId));
                }

                for (String id : uniqueIds) {
                    writer.println(id);
                }

                writer.println();
                writer.println("# Total registered voters: " + MAX_VOTERS);
            }

            System.out.println("Created voters database with " + MAX_VOTERS + " registered voters");
        }
    }

    /**
     * Initialize admin credentials file
     */
    private void initializeAdminFile() throws IOException {
        File adminFile = new File(ADMIN_FILE);

        if (!adminFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(adminFile))) {
                writer.println("# Admin Credentials");
                writer.println("# Format: USERNAME:PASSWORD");
                writer.println(ADMIN_USERNAME + ":" + ADMIN_PASSWORD);
            }

            System.out.println("Created admin credentials file");
        }
    }

    /**
     * Initialize candidates file
     */
    private void initializeCandidatesFile() throws IOException {
        File candidatesFile = new File(CANDIDATES_FILE);

        if (!candidatesFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(candidatesFile))) {
                writer.println("# Candidates Database");
                writer.println("# Format: ID:NAME:PARTY");
                writer.println("# Add candidates through the admin panel");
                writer.println();
            }

            System.out.println("Created candidates file");
        }
    }

    /**
     * Initialize votes file
     */
    private void initializeVotesFile() throws IOException {
        File votesFile = new File(VOTES_FILE);

        if (!votesFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(votesFile))) {
                writer.println("# Vote Records");
                writer.println("# Format: TIMESTAMP:VOTER_HASH:CANDIDATE_ID");
                writer.println("# Voter identity is hashed for anonymity");
                writer.println();
            }

            System.out.println("Created votes file");
        }
    }

    /**
     * Initialize session status file
     */
    private void initializeSessionFile() throws IOException {
        File sessionFile = new File(SESSION_FILE);

        if (!sessionFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(sessionFile))) {
                writer.println("# Session Status");
                writer.println("# ACTIVE or INACTIVE");
                writer.println("INACTIVE");
                writer.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            System.out.println("Created session file");
        }
    }

    /**
     * Initialize activity log file
     */
    private void initializeActivityLog() throws IOException {
        File logFile = new File(ACTIVITY_LOG);

        if (!logFile.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile))) {
                writer.println("# Activity Log for Digital Voting System");
                writer.println("# Format: TIMESTAMP:ACTION:USER:DETAILS");
                writer.println();
            }

            System.out.println("Created activity log file");
        }
    }

    /**
     * Load all system data from files
     */
    public void loadSystemData() throws IOException {
        loadVotersData();
        loadCandidatesData();
        loadVotesData();
        loadSessionStatus();
        loadRecentActivity();

        System.out.println("System data loaded successfully");
        System.out.println("Registered voters: " + registeredVoters.size());
        System.out.println("Candidates: " + candidates.size());
        System.out.println("Votes cast: " + votedVoters.size());
        System.out.println("Session active: " + sessionActive);
    }

    /**
     * Load voters data from file
     */
    private void loadVotersData() throws IOException {
        registeredVoters.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(VOTERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    if (line.matches("\\d{8}")) { // 8-digit validation
                        registeredVoters.add(line);
                    }
                }
            }
        }
    }

    /**
     * Load candidates data from file
     */
    private void loadCandidatesData() throws IOException {
        candidates.clear();
        voteCount.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(CANDIDATES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split(":");
                    if (parts.length >= 3) {
                        String id = parts[0];
                        String name = parts[1];
                        String party = parts[2];
                        candidates.put(id, new String[]{name, party});
                        voteCount.put(id, 0); // Initialize vote count
                    }
                }
            }
        }
    }

    /**
     * Load votes data and count votes
     */
    private void loadVotesData() throws IOException {
        votedVoters.clear();

        // Reset vote counts
        for (String candidateId : candidates.keySet()) {
            voteCount.put(candidateId, 0);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(VOTES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split(":");
                    if (parts.length >= 3) {
                        String voterHash = parts[1];
                        String candidateId = parts[2];

                        votedVoters.add(voterHash);

                        // Increment vote count for candidate
                        if (voteCount.containsKey(candidateId)) {
                            voteCount.put(candidateId, voteCount.get(candidateId) + 1);
                        }
                    }
                }
            }
        }
    }

    /**
     * Load session status
     */
    private void loadSessionStatus() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    sessionActive = "ACTIVE".equals(line);
                    break;
                }
            }
        }
    }

    /**
     * Load recent activity from log
     */
    private void loadRecentActivity() throws IOException {
        activityLog.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(ACTIVITY_LOG))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    activityLog.add(line);
                }
            }
        }

        // Keep only last 100 entries to prevent memory issues
        if (activityLog.size() > 100) {
            activityLog = new ArrayList<>(activityLog.subList(activityLog.size() - 100, activityLog.size()));
        }
    }

    /**
     * Check if voter is registered in the system
     */
    public boolean isVoterRegistered(String voterID) {
        return registeredVoters.contains(voterID);
    }

    /**
     * Check if voter has already voted (using hash for anonymity)
     */
    public boolean hasVoterVoted(String voterID) {
        String voterHash = hashVoterID(voterID);
        return votedVoters.contains(voterHash);
    }

    /**
     * Hash voter ID for anonymity (simple hash for demo)
     */
    private String hashVoterID(String voterID) {
        return "VOTER_" + Math.abs(voterID.hashCode());
    }

    /**
     * Record a vote
     */
    public boolean recordVote(String voterID, String candidateID) throws IOException {
        if (!isVoterRegistered(voterID)) {
            return false;
        }

        if (hasVoterVoted(voterID)) {
            return false;
        }

        if (!candidates.containsKey(candidateID)) {
            return false;
        }

        if (!sessionActive) {
            return false;
        }

        // Record the vote
        String voterHash = hashVoterID(voterID);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        try (PrintWriter writer = new PrintWriter(new FileWriter(VOTES_FILE, true))) {
            writer.println(timestamp + ":" + voterHash + ":" + candidateID);
        }

        // Update in-memory data
        votedVoters.add(voterHash);
        voteCount.put(candidateID, voteCount.get(candidateID) + 1);

        return true;
    }

    /**
     * Add a new candidate
     */
    public boolean addCandidate(String candidateID, String name, String party) throws IOException {
        if (candidates.containsKey(candidateID)) {
            return false; // Candidate already exists
        }

        // Add to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(CANDIDATES_FILE, true))) {
            writer.println(candidateID + ":" + name + ":" + party);
        }

        // Update in-memory data
        candidates.put(candidateID, new String[]{name, party});
        voteCount.put(candidateID, 0);

        return true;
    }

    /**
     * Get all candidates as formatted strings
     */
    public List<String> getAllCandidates() {
        List<String> candidateList = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : candidates.entrySet()) {
            String id = entry.getKey();
            String[] info = entry.getValue();
            String formatted = id + " - " + info[0] + " (" + info[1] + ")";
            candidateList.add(formatted);
        }

        return candidateList;
    }

    /**
     * Get candidate count
     */
    public int getCandidateCount() {
        return candidates.size();
    }

    /**
     * Get total votes cast
     */
    public int getTotalVotes() {
        return votedVoters.size();
    }

    /**
     * Get voting results
     */
    public Map<String, Integer> getVotingResults() {
        Map<String, Integer> results = new HashMap<>();

        for (Map.Entry<String, String[]> entry : candidates.entrySet()) {
            String id = entry.getKey();
            String[] info = entry.getValue();
            String candidateName = info[0] + " (" + info[1] + ")";
            int votes = voteCount.getOrDefault(id, 0);
            results.put(candidateName, votes);
        }

        return results;
    }

    /**
     * Set session status
     */
    public void setSessionStatus(boolean active) throws IOException {
        sessionActive = active;

        try (PrintWriter writer = new PrintWriter(new FileWriter(SESSION_FILE))) {
            writer.println("# Session Status");
            writer.println("# ACTIVE or INACTIVE");
            writer.println(active ? "ACTIVE" : "INACTIVE");
            writer.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    /**
     * Check if session is active
     */
    public boolean isSessionActive() {
        return sessionActive;
    }

    /**
     * Clear all voting data (start fresh session)
     */
    public void clearVotingData() throws IOException {
        // Clear votes file
        try (PrintWriter writer = new PrintWriter(new FileWriter(VOTES_FILE))) {
            writer.println("# Vote Records");
            writer.println("# Format: TIMESTAMP:VOTER_HASH:CANDIDATE_ID");
            writer.println("# Voter identity is hashed for anonymity");
            writer.println();
        }

        // Reset in-memory data
        votedVoters.clear();
        for (String candidateId : candidates.keySet()) {
            voteCount.put(candidateId, 0);
        }

        logActivity("DATA_CLEAR", "SYSTEM", "All voting data cleared for new session");
    }

    /**
     * Log activity for audit trail
     */
    public void logActivity(String action, String user, String details) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String logEntry = timestamp + ":" + action + ":" + user + ":" + details;

        // Add to memory
        activityLog.add(logEntry);

        // Write to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACTIVITY_LOG, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to activity log: " + e.getMessage());
        }

        // Keep memory log manageable
        if (activityLog.size() > 100) {
            activityLog.remove(0);
        }
    }

    /**
     * Get recent activity entries
     */
    public List<String> getRecentActivity(int count) {
        int startIndex = Math.max(0, activityLog.size() - count);
        return new ArrayList<>(activityLog.subList(startIndex, activityLog.size()));
    }

    /**
     * Save current system state
     */
    public void saveSystemState() throws IOException {
        // Session state is automatically saved when changed
        // Activity log is automatically appended
        // Vote data is automatically saved when recorded

        logActivity("STATE_SAVE", "SYSTEM", "System state saved successfully");
    }

    /**
     * Get registered voters list (for admin purposes)
     */
    public List<String> getRegisteredVoters() {
        return new ArrayList<>(registeredVoters);
    }

    /**
     * Add more voters to the system (admin function)
     */
    public boolean addVoter(String voterID) throws IOException {
        if (voterID.length() != 8 || !voterID.matches("\\d+")) {
            return false;
        }

        if (registeredVoters.contains(voterID)) {
            return false; // Already registered
        }

        if (registeredVoters.size() >= MAX_VOTERS) {
            return false; // Maximum capacity reached
        }

        // Add to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(VOTERS_FILE, true))) {
            writer.println(voterID);
        }

        // Update in-memory
        registeredVoters.add(voterID);

        return true;
    }

    /**
     * Remove voter from system (admin function)
     */
    public boolean removeVoter(String voterID) throws IOException {
        if (!registeredVoters.contains(voterID)) {
            return false;
        }

        // Remove from memory
        registeredVoters.remove(voterID);

        // Rewrite the entire file
        try (PrintWriter writer = new PrintWriter(new FileWriter(VOTERS_FILE))) {
            writer.println("# Registered Voters Database");
            writer.println("# Format: AadhaarID");
            writer.println("# Each ID is 8 digits, pre-registered for voting");
            writer.println();

            for (String id : registeredVoters) {
                writer.println(id);
            }

            writer.println();
            writer.println("# Total registered voters: " + registeredVoters.size());
        }

        return true;
    }

    /**
     * Validate admin credentials
     */
    public boolean validateAdminCredentials(String username, String password) {
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    /**
     * Get system statistics
     */
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRegisteredVoters", registeredVoters.size());
        stats.put("totalCandidates", candidates.size());
        stats.put("totalVotesCast", votedVoters.size());
        stats.put("sessionActive", sessionActive);
        stats.put("voterTurnout", registeredVoters.size() > 0 ? 
                  (votedVoters.size() * 100.0 / registeredVoters.size()) : 0.0);

        return stats;
    }

    /**
     * Export voting data to CSV for analysis
     */
    public void exportResultsToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Candidate_ID,Candidate_Name,Party,Vote_Count,Percentage");

            int totalVotes = votedVoters.size();

            for (Map.Entry<String, String[]> entry : candidates.entrySet()) {
                String id = entry.getKey();
                String[] info = entry.getValue();
                int votes = voteCount.getOrDefault(id, 0);
                double percentage = totalVotes > 0 ? (votes * 100.0 / totalVotes) : 0.0;

                writer.printf("%s,%s,%s,%d,%.2f%%\n", 
                            id, info[0], info[1], votes, percentage);
            }
        }

        logActivity("EXPORT_CSV", "SYSTEM", "Results exported to " + filename);
    }

    /**
     * Get detailed voting statistics
     */
    public String getDetailedStatistics() {
        StringBuilder stats = new StringBuilder();
        Map<String, Object> systemStats = getSystemStatistics();

        stats.append("DIGITAL VOTING SYSTEM - DETAILED STATISTICS\n");
        stats.append("==========================================\n\n");
        stats.append("System Overview:\n");
        stats.append("- Total Registered Voters: ").append(systemStats.get("totalRegisteredVoters")).append("\n");
        stats.append("- Total Candidates: ").append(systemStats.get("totalCandidates")).append("\n");
        stats.append("- Total Votes Cast: ").append(systemStats.get("totalVotesCast")).append("\n");
        stats.append("- Voter Turnout: ").append(String.format("%.2f%%", systemStats.get("voterTurnout"))).append("\n");
        stats.append("- Session Status: ").append(sessionActive ? "ACTIVE" : "INACTIVE").append("\n\n");

        stats.append("Vote Distribution:\n");
        Map<String, Integer> results = getVotingResults();
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            stats.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" votes\n");
        }

        return stats.toString();
    }
}
