package utils;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import main.VotingSystemApp;

public class VotingPanels {

    private VotingSystemApp app;
    private DataManager dataManager;
    private VotingSecurityManager securityManager;

    private Panel loginPanel, adminPanel, voterPanel, votingPanel, resultsPanel;
    private TextField loginUsernameField, loginPasswordField;
    private Choice userTypeChoice;
    private Button loginButton, exitButton;

    private TextArea adminStatusArea;
    private TextField candidateIdField, candidateNameField, candidatePartyField;
    private Button addCandidateButton, startSessionButton, stopSessionButton, viewResultsButton, logoutAdminButton;
    private java.awt.List candidatesList;

    private TextField voterIdField;
    private Button voterLoginButton, backToMainButton;
    private TextArea voterInfoArea;

    private java.awt.List votingCandidatesList;
    private Button castVoteButton, cancelVoteButton;
    private Label selectedCandidateLabel;
    private String selectedCandidateId = "";

    private TextArea resultsArea;
    private Button refreshResultsButton, backFromResultsButton;
    private Canvas resultsChart;

    public VotingPanels(VotingSystemApp app, DataManager dataManager, VotingSecurityManager securityManager) {
        this.app = app;
        this.dataManager = dataManager;
        this.securityManager = securityManager;
        initializePanels();
    }

    private void initializePanels() {
        createLoginPanel();
        createAdminPanel();
        createVoterPanel();
        createVotingPanel();
        createResultsPanel();
    }

    private void createLoginPanel() {
        loginPanel = new Panel(new BorderLayout());
        loginPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);

        Panel titlePanel = new Panel(new FlowLayout());
        titlePanel.setBackground(VotingSystemApp.PRIMARY_COLOR);
        Label titleLabel = new Label("DIGITAL VOTING SYSTEM", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        Panel subtitlePanel = new Panel(new FlowLayout());
        subtitlePanel.setBackground(VotingSystemApp.PRIMARY_COLOR);
        Label subtitleLabel = new Label("Secure Electronic Voting Platform", Label.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        subtitlePanel.add(subtitleLabel);

        Panel headerPanel = new Panel(new BorderLayout());
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(subtitlePanel, BorderLayout.SOUTH);

        Panel formPanel = new Panel(new GridBagLayout());
        formPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        Label userTypeLabel = new Label("Login as:");
        userTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(userTypeLabel, gbc);
        gbc.gridx = 1;
        userTypeChoice = new Choice();
        userTypeChoice.add("Administrator");
        userTypeChoice.add("Voter");
        userTypeChoice.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(userTypeChoice, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        Label usernameLabel = new Label("Username/Aadhaar:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        loginUsernameField = new TextField(20);
        loginUsernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(loginUsernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        loginPasswordField = new TextField(20);
        loginPasswordField.setEchoChar('*');
        loginPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(loginPasswordField, gbc);

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        loginButton = new Button("LOGIN");
        loginButton.setBackground(VotingSystemApp.SUCCESS_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(new LoginActionListener());
        exitButton = new Button("EXIT");
        exitButton.setBackground(VotingSystemApp.ACCENT_COLOR);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.addActionListener(e -> app.actionPerformed(new ActionEvent(this, 0, "EXIT")));
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        loginPanel.add(headerPanel, BorderLayout.NORTH);
        loginPanel.add(formPanel, BorderLayout.CENTER);
        
        Panel instructionPanel = new Panel(new FlowLayout());
        instructionPanel.setBackground(new Color(255, 252, 230));
        Label instructionLabel = new Label("Default Admin: admin/admin123 | Voters: Use 8-digit Aadhaar ID");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(new Color(102, 77, 3));
        instructionPanel.add(instructionLabel);

        Panel bottomPanel = new Panel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(instructionPanel, BorderLayout.SOUTH);
        loginPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createAdminPanel() {
        adminPanel = new Panel(new BorderLayout());
        adminPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);

        Panel headerPanel = new Panel(new FlowLayout());
        headerPanel.setBackground(VotingSystemApp.PRIMARY_COLOR);
        Label headerLabel = new Label("ADMINISTRATOR DASHBOARD", Label.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        Panel mainContent = new Panel(new BorderLayout());
        Panel leftPanel = new Panel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        Label sessionLabel = new Label("SESSION CONTROL");
        sessionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sessionLabel.setAlignment(Label.CENTER);
        Panel sessionButtonPanel = new Panel(new GridLayout(4, 1, 5, 5));
        startSessionButton = new Button("START VOTING SESSION");
        startSessionButton.setBackground(VotingSystemApp.SUCCESS_COLOR);
        startSessionButton.setForeground(Color.WHITE);
        startSessionButton.setFont(new Font("Arial", Font.BOLD, 12));
        startSessionButton.addActionListener(new AdminActionListener());
        stopSessionButton = new Button("STOP VOTING SESSION");
        stopSessionButton.setBackground(VotingSystemApp.ACCENT_COLOR);
        stopSessionButton.setForeground(Color.WHITE);
        stopSessionButton.setFont(new Font("Arial", Font.BOLD, 12));
        stopSessionButton.addActionListener(new AdminActionListener());
        viewResultsButton = new Button("VIEW RESULTS");
        viewResultsButton.setBackground(VotingSystemApp.PRIMARY_COLOR);
        viewResultsButton.setForeground(Color.WHITE);
        viewResultsButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewResultsButton.addActionListener(new AdminActionListener());
        logoutAdminButton = new Button("LOGOUT");
        logoutAdminButton.setBackground(new Color(108, 117, 125));
        logoutAdminButton.setForeground(Color.WHITE);
        logoutAdminButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutAdminButton.addActionListener(e -> app.logout());
        sessionButtonPanel.add(startSessionButton);
        sessionButtonPanel.add(stopSessionButton);
        sessionButtonPanel.add(viewResultsButton);
        sessionButtonPanel.add(logoutAdminButton);
        leftPanel.add(sessionLabel, BorderLayout.NORTH);
        leftPanel.add(sessionButtonPanel, BorderLayout.CENTER);

        Panel centerPanel = new Panel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        Label candidateLabel = new Label("CANDIDATE MANAGEMENT");
        candidateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        candidateLabel.setAlignment(Label.CENTER);
        Panel candidateForm = new Panel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        candidateForm.add(new Label("Candidate ID:"), gbc);
        gbc.gridx = 1;
        candidateIdField = new TextField(15);
        candidateForm.add(candidateIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        candidateForm.add(new Label("Name:"), gbc);
        gbc.gridx = 1;
        candidateNameField = new TextField(15);
        candidateForm.add(candidateNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        candidateForm.add(new Label("Party:"), gbc);
        gbc.gridx = 1;
        candidatePartyField = new TextField(15);
        candidateForm.add(candidatePartyField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        addCandidateButton = new Button("ADD CANDIDATE");
        addCandidateButton.setBackground(VotingSystemApp.SUCCESS_COLOR);
        addCandidateButton.setForeground(Color.WHITE);
        addCandidateButton.addActionListener(new AdminActionListener());
        candidateForm.add(addCandidateButton, gbc);
        candidatesList = new java.awt.List(8);
        candidatesList.setFont(new Font("Monospace", Font.PLAIN, 12));
        Panel candidateListPanel = new Panel(new BorderLayout());
        candidateListPanel.add(new Label("Current Candidates:"), BorderLayout.NORTH);
        candidateListPanel.add(candidatesList, BorderLayout.CENTER);
        centerPanel.add(candidateLabel, BorderLayout.NORTH);
        centerPanel.add(candidateForm, BorderLayout.CENTER);
        centerPanel.add(candidateListPanel, BorderLayout.SOUTH);

        Panel rightPanel = new Panel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        Label statusLabel = new Label("SYSTEM STATUS");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setAlignment(Label.CENTER);
        adminStatusArea = new TextArea(15, 30);
        adminStatusArea.setEditable(false);
        adminStatusArea.setFont(new Font("Monospace", Font.PLAIN, 11));
        adminStatusArea.setBackground(new Color(248, 249, 250));
        rightPanel.add(statusLabel, BorderLayout.NORTH);
        rightPanel.add(adminStatusArea, BorderLayout.CENTER);

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(centerPanel, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);

        adminPanel.add(headerPanel, BorderLayout.NORTH);
        adminPanel.add(mainContent, BorderLayout.CENTER);
    }

    private void createVoterPanel() {
        voterPanel = new Panel(new BorderLayout());
        voterPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        Panel headerPanel = new Panel(new FlowLayout());
        headerPanel.setBackground(VotingSystemApp.PRIMARY_COLOR);
        Label headerLabel = new Label("VOTER AUTHENTICATION", Label.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        Panel formPanel = new Panel(new GridBagLayout());
        formPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        Label instructionLabel = new Label("Enter your 8-digit Aadhaar ID to proceed to voting");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        instructionLabel.setForeground(VotingSystemApp.PRIMARY_COLOR);
        formPanel.add(instructionLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        Label idLabel = new Label("Aadhaar ID (8 digits):");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        voterIdField = new TextField(20);
        voterIdField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(voterIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        voterInfoArea = new TextArea(8, 40);
        voterInfoArea.setEditable(false);
        voterInfoArea.setBackground(new Color(255, 252, 230));
        voterInfoArea.setFont(new Font("Arial", Font.PLAIN, 12));
        voterInfoArea.setText("\nWelcome to Digital Voting System\n\nSecurity Features:\n• Each Aadhaar ID can vote only once per session\n• Votes are encrypted and tamper-proof\n• Complete anonymity maintained\n• Real-time validation and verification\n\nPlease enter your 8-digit Aadhaar ID to continue.");
        formPanel.add(voterInfoArea, gbc);

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        voterLoginButton = new Button("PROCEED TO VOTE");
        voterLoginButton.setBackground(VotingSystemApp.SUCCESS_COLOR);
        voterLoginButton.setForeground(Color.WHITE);
        voterLoginButton.setFont(new Font("Arial", Font.BOLD, 14));
        voterLoginButton.addActionListener(new VoterActionListener());
        backToMainButton = new Button("BACK TO LOGIN");
        backToMainButton.setBackground(VotingSystemApp.PRIMARY_COLOR);
        backToMainButton.setForeground(Color.WHITE);
        backToMainButton.setFont(new Font("Arial", Font.BOLD, 14));
        backToMainButton.addActionListener(e -> app.showPanel(VotingSystemApp.LOGIN_PANEL));
        buttonPanel.add(voterLoginButton);
        buttonPanel.add(backToMainButton);

        voterPanel.add(headerPanel, BorderLayout.NORTH);
        voterPanel.add(formPanel, BorderLayout.CENTER);
        voterPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createVotingPanel() {
        votingPanel = new Panel(new BorderLayout());
        votingPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        Panel headerPanel = new Panel(new FlowLayout());
        headerPanel.setBackground(VotingSystemApp.SUCCESS_COLOR);
        Label headerLabel = new Label("CAST YOUR VOTE", Label.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        Panel instructionPanel = new Panel(new FlowLayout());
        instructionPanel.setBackground(new Color(217, 237, 247));
        Label instructionLabel = new Label("Select a candidate from the list below and click 'CAST VOTE'");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        instructionLabel.setForeground(VotingSystemApp.PRIMARY_COLOR);
        instructionPanel.add(instructionLabel);

        Panel candidatePanel = new Panel(new BorderLayout());
        candidatePanel.setBackground(Color.WHITE);
        Label candidateLabel = new Label("CANDIDATES LIST");
        candidateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        candidateLabel.setAlignment(Label.CENTER);
        candidateLabel.setForeground(VotingSystemApp.PRIMARY_COLOR);
        votingCandidatesList = new java.awt.List(10);
        votingCandidatesList.setFont(new Font("Arial", Font.PLAIN, 14));
        votingCandidatesList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selected = votingCandidatesList.getSelectedItem();
                    if (selected != null) {
                        selectedCandidateId = selected.split(" - ")[0];
                        selectedCandidateLabel.setText("Selected: " + selected);
                        castVoteButton.setEnabled(true);
                    }
                }
            }
        });
        candidatePanel.add(candidateLabel, BorderLayout.NORTH);
        candidatePanel.add(votingCandidatesList, BorderLayout.CENTER);
        selectedCandidateLabel = new Label("No candidate selected", Label.CENTER);
        selectedCandidateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectedCandidateLabel.setForeground(VotingSystemApp.ACCENT_COLOR);

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        castVoteButton = new Button("CAST VOTE");
        castVoteButton.setBackground(VotingSystemApp.SUCCESS_COLOR);
        castVoteButton.setForeground(Color.WHITE);
        castVoteButton.setFont(new Font("Arial", Font.BOLD, 16));
        castVoteButton.setEnabled(false);
        castVoteButton.addActionListener(new VotingActionListener());
        cancelVoteButton = new Button("CANCEL");
        cancelVoteButton.setBackground(VotingSystemApp.ACCENT_COLOR);
        cancelVoteButton.setForeground(Color.WHITE);
        cancelVoteButton.setFont(new Font("Arial", Font.BOLD, 16));
        cancelVoteButton.addActionListener(e -> app.showPanel(VotingSystemApp.LOGIN_PANEL));
        buttonPanel.add(castVoteButton);
        buttonPanel.add(cancelVoteButton);

        Panel topPanel = new Panel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(instructionPanel, BorderLayout.SOUTH);

        votingPanel.add(topPanel, BorderLayout.NORTH);
        votingPanel.add(candidatePanel, BorderLayout.CENTER);

        Panel bottomPanel = new Panel(new BorderLayout());
        bottomPanel.add(selectedCandidateLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        votingPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void createResultsPanel() {
        resultsPanel = new Panel(new BorderLayout());
        resultsPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        Panel headerPanel = new Panel(new FlowLayout());
        headerPanel.setBackground(VotingSystemApp.PRIMARY_COLOR);
        Label headerLabel = new Label("ELECTION RESULTS", Label.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        resultsArea = new TextArea(20, 60);
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        resultsArea.setBackground(Color.WHITE);
        resultsChart = new Canvas() {
            @Override
            public void paint(Graphics g) {
                drawResultsChart(g);
            }
        };
        resultsChart.setSize(400, 200);
        resultsChart.setBackground(Color.WHITE);

        Panel buttonPanel = new Panel(new FlowLayout());
        buttonPanel.setBackground(VotingSystemApp.SECONDARY_COLOR);
        refreshResultsButton = new Button("REFRESH RESULTS");
        refreshResultsButton.setBackground(VotingSystemApp.PRIMARY_COLOR);
        refreshResultsButton.setForeground(Color.WHITE);
        refreshResultsButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshResultsButton.addActionListener(new ResultsActionListener());
        backFromResultsButton = new Button("BACK");
        backFromResultsButton.setBackground(VotingSystemApp.ACCENT_COLOR);
        backFromResultsButton.setForeground(Color.WHITE);
        backFromResultsButton.setFont(new Font("Arial", Font.BOLD, 14));
        backFromResultsButton.addActionListener(new ResultsActionListener());
        buttonPanel.add(refreshResultsButton);
        buttonPanel.add(backFromResultsButton);

        Panel contentPanel = new Panel(new BorderLayout());
        contentPanel.add(resultsArea, BorderLayout.CENTER);
        contentPanel.add(resultsChart, BorderLayout.EAST);

        resultsPanel.add(headerPanel, BorderLayout.NORTH);
        resultsPanel.add(contentPanel, BorderLayout.CENTER);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void drawResultsChart(Graphics g) {
        try {
            Map<String, Integer> results = dataManager.getVotingResults();
            if (results.isEmpty()) {
                g.setColor(Color.GRAY);
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString("No votes cast yet", 50, 100);
                return;
            }
            int y = 30;
            int maxVotes = results.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                String candidate = entry.getKey();
                int votes = entry.getValue();
                g.setColor(Color.BLACK);
                g.drawString(candidate, 10, y);
                int barWidth = (votes * 300) / maxVotes;
                g.setColor(VotingSystemApp.PRIMARY_COLOR);
                g.fillRect(10, y + 5, barWidth, 20);
                g.setColor(Color.WHITE);
                g.drawString(String.valueOf(votes), 15, y + 18);
                y += 40;
            }
        } catch (Exception e) {
            g.setColor(Color.RED);
            g.drawString("Error loading results", 50, 100);
        }
    }

    public void updatePanelStates() {
        updateAdminPanel();
        updateVotingPanel();
        updateResultsPanel();
    }

    private void updateAdminPanel() {
        try {
            StringBuilder status = new StringBuilder();
            status.append("SYSTEM STATUS\n");
            status.append("=============\n");
            status.append("Session Active: ").append(app.isSessionActive() ? "YES" : "NO").append("\n");
            status.append("Current User: ").append(app.getCurrentUser()).append("\n");
            status.append("Total Candidates: ").append(dataManager.getCandidateCount()).append("\n");
            status.append("Total Votes: ").append(dataManager.getTotalVotes()).append("\n\n");
            status.append("RECENT ACTIVITY\n");
            status.append("===============\n");
            for (String activity : dataManager.getRecentActivity(5)) {
                status.append(activity).append("\n");
            }
            adminStatusArea.setText(status.toString());
            candidatesList.removeAll();
            for (String candidate : dataManager.getAllCandidates()) {
                candidatesList.add(candidate);
            }
            boolean sessionActive = app.isSessionActive();
            startSessionButton.setEnabled(!sessionActive);
            stopSessionButton.setEnabled(sessionActive);
        } catch (Exception e) {
            adminStatusArea.setText("Error updating admin panel: " + e.getMessage());
        }
    }

    private void updateVotingPanel() {
        try {
            votingCandidatesList.removeAll();
            if (!app.isSessionActive()) {
                votingCandidatesList.add("No active voting session");
                castVoteButton.setEnabled(false);
                return;
            }
            java.util.List<String> candidates = dataManager.getAllCandidates();
            for (String candidate : candidates) {
                votingCandidatesList.add(candidate);
            }
            if (candidates.isEmpty()) {
                votingCandidatesList.add("No candidates available");
                castVoteButton.setEnabled(false);
            }
        } catch (Exception e) {
            votingCandidatesList.removeAll();
            votingCandidatesList.add("Error loading candidates");
            castVoteButton.setEnabled(false);
        }
    }

    private void updateResultsPanel() {
        try {
            Map<String, Integer> results = dataManager.getVotingResults();
            StringBuilder resultText = new StringBuilder();
            resultText.append("ELECTION RESULTS SUMMARY\n");
            resultText.append("========================\n\n");
            if (results.isEmpty()) {
                resultText.append("No votes have been cast yet.\n");
            } else {
                int totalVotes = results.values().stream().mapToInt(Integer::intValue).sum();
                resultText.append("Total Votes Cast: ").append(totalVotes).append("\n\n");
                String winner = results.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("No winner");
                int maxVotes = results.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                resultText.append("WINNER: ").append(winner).append(" (").append(maxVotes).append(" votes)\n");
                for (int i = 0; i < 50; i++) resultText.append("=");
                resultText.append("\n\n");
                resultText.append("DETAILED RESULTS:\n");
                for (Map.Entry<String, Integer> entry : results.entrySet()) {
                    String candidate = entry.getKey();
                    int votes = entry.getValue();
                    double percentage = (totalVotes > 0) ? (votes * 100.0 / totalVotes) : 0;
                    resultText.append(String.format("%-30s: %3d votes (%.1f%%)\n", candidate, votes, percentage));
                }
            }
            resultText.append("\n\nSession Status: ").append(app.isSessionActive() ? "ACTIVE" : "CLOSED");
            resultText.append("\nLast Updated: ").append(java.time.LocalDateTime.now());
            resultsArea.setText(resultText.toString());
            resultsChart.repaint();
        } catch (Exception e) {
            resultsArea.setText("Error loading results: " + e.getMessage());
        }
    }

    public void resetPanels() {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        userTypeChoice.select(0);
        voterIdField.setText("");
        selectedCandidateId = "";
        selectedCandidateLabel.setText("No candidate selected");
        castVoteButton.setEnabled(false);
        candidateIdField.setText("");
        candidateNameField.setText("");
        candidatePartyField.setText("");
    }

    public Panel getLoginPanel() { return loginPanel; }
    public Panel getAdminPanel() { return adminPanel; }
    public Panel getVoterPanel() { return voterPanel; }
    public Panel getVotingPanel() { return votingPanel; }
    public Panel getResultsPanel() { return resultsPanel; }

    // --- Event Listeners ---

    private class LoginActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = loginUsernameField.getText().trim();
            String password = loginPasswordField.getText();
            String userType = userTypeChoice.getSelectedItem();
            if (username.isEmpty()) {
                app.showErrorDialog("Input Error", "Please enter username/Aadhaar ID");
                return;
            }
            if ("Administrator".equals(userType)) {
                if (password.isEmpty()) {
                    app.showErrorDialog("Input Error", "Please enter password for admin login");
                    return;
                }
                if (app.authenticateUser(username, password, "ADMIN")) {
                    app.showPanel(VotingSystemApp.ADMIN_PANEL);
                    updateAdminPanel();
                } else {
                    app.showErrorDialog("Authentication Failed", "Invalid admin credentials");
                }
            } else {
                if (username.length() != 8 || !username.matches("\\\\d+")) {
                    app.showErrorDialog("Invalid Aadhaar", "Aadhaar ID must be exactly 8 digits");
                    return;
                }
                app.showPanel(VotingSystemApp.VOTER_PANEL);
                voterIdField.setText(username);
            }
        }
    }

    private class AdminActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = ((Button) e.getSource()).getLabel();
            switch (command) {
                case "START VOTING SESSION":
                    app.startVotingSession();
                    break;
                case "STOP VOTING SESSION":
                    app.stopVotingSession();
                    break;
                case "VIEW RESULTS":
                    updateResultsPanel();
                    app.showPanel(VotingSystemApp.RESULTS_PANEL);
                    break;
                case "ADD CANDIDATE":
                    String id = candidateIdField.getText().trim();
                    String name = candidateNameField.getText().trim();
                    String party = candidatePartyField.getText().trim();
                    if (id.isEmpty() || name.isEmpty() || party.isEmpty()) {
                        app.showErrorDialog("Input Error", "Please fill all candidate fields");
                        return;
                    }
                    if (app.addCandidate(id, name, party)) {
                        candidateIdField.setText("");
                        candidateNameField.setText("");
                        candidatePartyField.setText("");
                        app.showInfoDialog("Success", "Candidate added successfully");
                    }
                    break;
            }
        }
    }

    private class VoterActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String voterID = voterIdField.getText().trim();
            if (voterID.length() != 8 || !voterID.matches("\\\\d+")) {
                app.showErrorDialog("Invalid Aadhaar", "Aadhaar ID must be exactly 8 digits");
                return;
            }
            if (!app.isSessionActive()) {
                app.showErrorDialog("Session Inactive", "No voting session is currently active");
                return;
            }
            try {
                if (dataManager.hasVoterVoted(voterID)) {
                    app.showErrorDialog("Already Voted", "This Aadhaar ID has already been used to vote in this session");
                    return;
                }
                if (app.authenticateUser(voterID, "", "VOTER")) {
                    updateVotingPanel();
                    app.showPanel(VotingSystemApp.VOTING_PANEL);
                } else {
                    app.showErrorDialog("Authentication Failed", "Voter authentication failed");
                }
            } catch (Exception ex) {
                app.showErrorDialog("Error", "Authentication error: " + ex.getMessage());
            }
        }
    }

    private class VotingActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (selectedCandidateId.isEmpty()) {
                app.showErrorDialog("No Selection", "Please select a candidate first");
                return;
            }
            String voterID = app.getCurrentUser();
            if (app.castVote(voterID, selectedCandidateId)) {
                app.showInfoDialog("Vote Cast", "Thank you! Your vote has been recorded successfully.");
                app.logout();
            }
        }
    }

    private class ResultsActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String cmd = ((Button) e.getSource()).getLabel();
            if ("REFRESH RESULTS".equals(cmd)) {
                updateResultsPanel();
            } else if ("BACK".equals(cmd)) {
                if ("ADMIN".equals(app.getCurrentUserType())) {
                    app.showPanel(VotingSystemApp.ADMIN_PANEL);
                } else {
                    app.showPanel(VotingSystemApp.LOGIN_PANEL);
                }
            }
        }
    }
}
