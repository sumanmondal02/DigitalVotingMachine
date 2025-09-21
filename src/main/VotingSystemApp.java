package main;

import java.awt.*;
import java.awt.event.*;
import utils.*;

public class VotingSystemApp extends Frame implements ActionListener, WindowListener {

    // Core Components
    private CardLayout cardLayout;
    private Panel mainPanel;
    private VotingPanels panels;
    private DataManager dataManager;
    private VotingSecurityManager securityManager;  // Fixed: Renamed to avoid conflict

    // Application Constants
    public static final String APP_TITLE = "Digital Voting System - Professional Edition";
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    public static final Color PRIMARY_COLOR = new Color(41, 84, 144);
    public static final Color SECONDARY_COLOR = new Color(247, 247, 247);
    public static final Color ACCENT_COLOR = new Color(220, 53, 69);
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);

    // Panel Names for CardLayout Navigation
    public static final String LOGIN_PANEL = "LOGIN_PANEL";
    public static final String ADMIN_PANEL = "ADMIN_PANEL";
    public static final String VOTER_PANEL = "VOTER_PANEL";
    public static final String VOTING_PANEL = "VOTING_PANEL";
    public static final String RESULTS_PANEL = "RESULTS_PANEL";

    // Application State
    private boolean isSessionActive = false;
    private String currentUser = "";
    private String currentUserType = "";

    /**
     * Constructor - Initialize the Digital Voting System
     */
    public VotingSystemApp() {
        super(APP_TITLE);
        initializeSystem();
        setupGUI();
        loadInitialData();

        System.out.println("Digital Voting System initialized successfully!");
        System.out.println("System Features:");
        System.out.println("- Secure Admin Authentication");
        System.out.println("- 8-digit Aadhaar Voter Registration");
        System.out.println("- Real-time Vote Counting");
        System.out.println("- Tamper-proof Data Storage");
        System.out.println("- Professional AWT Interface");
    }

    /**
     * Initialize core system components
     */
    private void initializeSystem() {
        try {
            dataManager = new DataManager();
            securityManager = new VotingSecurityManager();  // Fixed: Use new class name
            securityManager.setDataManager(dataManager);    // Set reference
            panels = new VotingPanels(this, dataManager, securityManager);

            // Create data directory if it doesn't exist
            dataManager.initializeDataFiles();

        } catch (Exception e) {
            showErrorDialog("System Initialization Error", 
                          "Failed to initialize system components: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Setup the main GUI with CardLayout for panel switching
     */
    private void setupGUI() {
        // Set window properties
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(SECONDARY_COLOR);

        // Initialize CardLayout
        cardLayout = new CardLayout();
        mainPanel = new Panel(cardLayout);

        // Add all panels to the CardLayout
        mainPanel.add(panels.getLoginPanel(), LOGIN_PANEL);
        mainPanel.add(panels.getAdminPanel(), ADMIN_PANEL);
        mainPanel.add(panels.getVoterPanel(), VOTER_PANEL);
        mainPanel.add(panels.getVotingPanel(), VOTING_PANEL);
        mainPanel.add(panels.getResultsPanel(), RESULTS_PANEL);

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);

        // Add window listener for close operation
        addWindowListener(this);

        // Show login panel initially
        showPanel(LOGIN_PANEL);
    }

    /**
     * Load initial data and system state
     */
    private void loadInitialData() {
        try {
            // Load voting session status
            isSessionActive = dataManager.isSessionActive();

            // Load admin credentials and voter database
            dataManager.loadSystemData();

            // Update panel states based on loaded data
            panels.updatePanelStates();

        } catch (Exception e) {
            showErrorDialog("Data Loading Error", 
                          "Failed to load system data: " + e.getMessage());
        }
    }

    /**
     * Switch between different panels using CardLayout
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        // Update window title based on current panel
        String titleSuffix = "";
        switch (panelName) {
            case LOGIN_PANEL:
                titleSuffix = " - Login";
                break;
            case ADMIN_PANEL:
                titleSuffix = " - Administrator Dashboard";
                break;
            case VOTER_PANEL:
                titleSuffix = " - Voter Authentication";
                break;
            case VOTING_PANEL:
                titleSuffix = " - Cast Your Vote";
                break;
            case RESULTS_PANEL:
                titleSuffix = " - Election Results";
                break;
        }
        setTitle(APP_TITLE + titleSuffix);
    }

    /**
     * Handle user authentication
     */
    public boolean authenticateUser(String username, String password, String userType) {
        boolean authenticated = false;

        try {
            if ("ADMIN".equals(userType)) {
                authenticated = securityManager.authenticateAdmin(username, password);
            } else if ("VOTER".equals(userType)) {
                authenticated = securityManager.authenticateVoter(username);
            }

            if (authenticated) {
                currentUser = username;
                currentUserType = userType;

                // Log successful authentication
                dataManager.logActivity("LOGIN", currentUser, "Successful " + userType + " login");
            }

        } catch (Exception e) {
            showErrorDialog("Authentication Error", 
                          "Authentication failed: " + e.getMessage());
        }

        return authenticated;
    }

    /**
     * Start a new voting session (Admin only)
     */
    public boolean startVotingSession() {
        try {
            if (!"ADMIN".equals(currentUserType)) {
                showErrorDialog("Access Denied", "Only administrators can start voting sessions.");
                return false;
            }

            if (isSessionActive) {
                showErrorDialog("Session Active", "A voting session is already active.");
                return false;
            }

            // Clear previous voting data
            dataManager.clearVotingData();

            // Start new session
            isSessionActive = true;
            dataManager.setSessionStatus(true);

            // Log session start
            dataManager.logActivity("SESSION_START", currentUser, "Voting session started");

            showInfoDialog("Session Started", "Voting session has been started successfully!");
            panels.updatePanelStates();

            return true;

        } catch (Exception e) {
            showErrorDialog("Session Error", "Failed to start voting session: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stop the current voting session (Admin only)
     */
    public boolean stopVotingSession() {
        try {
            if (!"ADMIN".equals(currentUserType)) {
                showErrorDialog("Access Denied", "Only administrators can stop voting sessions.");
                return false;
            }

            if (!isSessionActive) {
                showErrorDialog("No Active Session", "No voting session is currently active.");
                return false;
            }

            // Stop session
            isSessionActive = false;
            dataManager.setSessionStatus(false);

            // Log session end
            dataManager.logActivity("SESSION_STOP", currentUser, "Voting session stopped");

            showInfoDialog("Session Stopped", "Voting session has been stopped successfully!");
            panels.updatePanelStates();

            return true;

        } catch (Exception e) {
            showErrorDialog("Session Error", "Failed to stop voting session: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cast a vote for a candidate
     */
    public boolean castVote(String voterID, String candidateID) {
        try {
            if (!isSessionActive) {
                showErrorDialog("Session Inactive", "No voting session is currently active.");
                return false;
            }

            // Check if voter has already voted
            if (dataManager.hasVoterVoted(voterID)) {
                showErrorDialog("Already Voted", 
                    "This Aadhaar ID has already been used to vote in this session.");
                return false;
            }

            // Record the vote
            boolean success = dataManager.recordVote(voterID, candidateID);

            if (success) {
                dataManager.logActivity("VOTE_CAST", voterID, "Vote cast for candidate: " + candidateID);
                showInfoDialog("Vote Recorded", "Your vote has been recorded successfully!");
                panels.updatePanelStates();
                return true;
            } else {
                showErrorDialog("Vote Error", "Failed to record your vote. Please try again.");
                return false;
            }

        } catch (Exception e) {
            showErrorDialog("Voting Error", "Error while casting vote: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a new candidate (Admin only)
     */
    public boolean addCandidate(String candidateID, String candidateName, String party) {
        try {
            if (!"ADMIN".equals(currentUserType)) {
                showErrorDialog("Access Denied", "Only administrators can add candidates.");
                return false;
            }

            boolean success = dataManager.addCandidate(candidateID, candidateName, party);

            if (success) {
                dataManager.logActivity("CANDIDATE_ADD", currentUser, 
                    "Added candidate: " + candidateName + " (" + party + ")");
                panels.updatePanelStates();
                return true;
            } else {
                showErrorDialog("Candidate Error", "Failed to add candidate. ID may already exist.");
                return false;
            }

        } catch (Exception e) {
            showErrorDialog("Candidate Error", "Error adding candidate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get current session status
     */
    public boolean isSessionActive() {
        return isSessionActive;
    }

    /**
     * Get current user information
     */
    public String getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserType() {
        return currentUserType;
    }

    /**
     * Logout current user and return to login screen
     */
    public void logout() {
        try {
            if (!currentUser.isEmpty()) {
                dataManager.logActivity("LOGOUT", currentUser, "User logged out");
                securityManager.removeActiveSession(currentUser);
            }

            currentUser = "";
            currentUserType = "";
            panels.resetPanels();
            showPanel(LOGIN_PANEL);

        } catch (Exception e) {
            showErrorDialog("Logout Error", "Error during logout: " + e.getMessage());
        }
    }

    /**
     * Show error dialog
     */
    public void showErrorDialog(String title, String message) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        Label messageLabel = new Label(message, Label.CENTER);
        messageLabel.setForeground(ACCENT_COLOR);

        Button okButton = new Button("OK");
        okButton.setBackground(ACCENT_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.addActionListener(e -> dialog.dispose());

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(okButton);

        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Show information dialog
     */
    public void showInfoDialog(String title, String message) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        Label messageLabel = new Label(message, Label.CENTER);
        messageLabel.setForeground(SUCCESS_COLOR);

        Button okButton = new Button("OK");
        okButton.setBackground(SUCCESS_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.addActionListener(e -> dialog.dispose());

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.add(okButton);

        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Clean shutdown of the application
     */
    private void shutdownApplication() {
        try {
            // Save current session state
            dataManager.saveSystemState();

            // Log shutdown
            if (!currentUser.isEmpty()) {
                dataManager.logActivity("SHUTDOWN", currentUser, "Application shutdown");
            }

            System.out.println("Digital Voting System shutdown successfully.");

        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        } finally {
            System.exit(0);
        }
    }

    /**
     * Show confirmation dialog
     */
    private int showConfirmDialog(String title, String message) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 150);
        dialog.setLocationRelativeTo(this);

        Label messageLabel = new Label(message, Label.CENTER);

        Panel buttonPanel = new Panel(new FlowLayout());
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        final int[] result = {0}; // 0=No, 1=Yes

        yesButton.addActionListener(e -> {
            result[0] = 1;
            dialog.dispose();
        });

        noButton.addActionListener(e -> {
            result[0] = 0;
            dialog.dispose();
        });

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);

        return result[0];
    }

    // ActionListener implementation
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle common actions that might be triggered from panels
        String command = e.getActionCommand();

        switch (command) {
            case "LOGOUT":
                logout();
                break;
            case "EXIT":
                shutdownApplication();
                break;
        }
    }

    // WindowListener implementation
    @Override
    public void windowClosing(WindowEvent e) {
        int choice = showConfirmDialog("Exit Application", 
                                     "Are you sure you want to exit the Digital Voting System?");
        if (choice == 1) { // Yes
            shutdownApplication();
        }
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}

    /**
     * Main method - Application entry point
     */
    public static void main(String[] args) {
        System.out.println("Starting Digital Voting System...");
        System.out.println("Initializing AWT Components...");

        try {
            VotingSystemApp app = new VotingSystemApp();
            app.setVisible(true);

            System.out.println("Application started successfully!");
            System.out.println("Default Admin Credentials:");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");

        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
