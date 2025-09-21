# Digital Voting System - Professional Edition

A comprehensive, secure electronic voting system built with Java AWT for academic and educational purposes.

## 🗳️ Overview

This Digital Voting System is a professional-grade application that provides:
- **Secure Admin Management**: Complete control over voting sessions and candidates
- **8-digit Aadhaar Authentication**: Pre-registered voter database with validation
- **Real-time Voting**: Live vote casting with immediate result updates
- **Tamper-proof Data Storage**: Encrypted and validated data persistence
- **Professional AWT GUI**: Clean, intuitive interface using CardLayout
- **Comprehensive Security**: Multi-layer authentication and audit trails

## 🚀 Quick Start

### Prerequisites
- **Java Development Kit (JDK) 8 or higher**
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 2GB RAM
- **Storage**: 50MB free space

### Installation Steps

1. **Download and Extract**
   ```bash
   # Extract the DigitalVotingSystem folder to your desired location
   cd DigitalVotingSystem
   ```

2. **Compile the Java Files**
   ```bash
   # Navigate to the src directory
   cd src

   # Compile all Java files
   javac -d ../build main/*.java utils/*.java
   ```

3. **Run the Application**
   ```bash
   # Navigate to build directory
   cd ../build

   # Run the main application
   java main.VotingSystemApp
   ```

### Alternative: One-Command Compilation and Execution
```bash
# From the project root directory
javac -cp . src/main/*.java src/utils/*.java && java -cp ./src main.VotingSystemApp
```

## 🔐 Default Credentials

### Administrator Access
- **Username**: `admin`
- **Password**: `admin123`

### Pre-registered Voters
The system comes with 20 pre-registered 8-digit Aadhaar IDs:
- Range: 10000000 - 99999999 (randomly generated)
- Access through: Login → Voter → Enter any pre-registered ID
- **Note**: Each ID can vote only once per session

## 📁 Project Structure

```
DigitalVotingSystem/
│
├── src/
│   ├── main/
│   │   └── VotingSystemApp.java     # Main application entry point
│   └── utils/
│       ├── DataManager.java         # Data persistence and file I/O
│       ├── SecurityManager.java     # Authentication and security
│       └── VotingPanels.java        # All GUI panels and components
│
├── data/                           # Auto-created at runtime
│   ├── voters.txt                  # Pre-registered voter database
│   ├── candidates.txt              # Candidate information
│   ├── votes.txt                   # Anonymous vote records
│   ├── session.txt                 # Current session status
│   ├── activity.log                # System activity audit trail
│   └── admin.txt                   # Admin credentials
│
├── build/                          # Compiled .class files (auto-created)
└── docs/
    ├── README.md                   # This file
    └── SECURITY.md                 # Security documentation
```

## 🎯 How to Use

### For Administrators

1. **Login as Administrator**
   - Select "Administrator" from login screen
   - Enter username: `admin` and password: `admin123`

2. **Manage Candidates**
   - Add candidates with unique IDs, names, and party affiliations
   - View current candidate list in real-time

3. **Session Control**
   - **Start Session**: Begin voting process (clears previous votes)
   - **Stop Session**: End voting and prevent further votes
   - **View Results**: See real-time voting results and statistics

4. **Monitor System**
   - View system status and recent activities
   - Check voter turnout and participation rates

### For Voters

1. **Voter Authentication**
   - Select "Voter" from login screen
   - Enter your 8-digit Aadhaar ID
   - System validates your registration status

2. **Cast Your Vote**
   - Browse available candidates
   - Select your preferred candidate
   - Confirm and cast your vote
   - Receive confirmation of successful voting

3. **Security Features**
   - One vote per Aadhaar ID per session
   - Anonymous voting (no link between voter and vote)
   - Real-time validation and verification

## 🔧 Configuration and Customization

### Adding More Voters

To add more voters to the system:

```java
// Through admin panel (future feature) or manually edit data/voters.txt
// Add 8-digit numbers, one per line
12345678
87654321
```

### Removing Voters

Edit `data/voters.txt` and remove the specific Aadhaar ID lines.

### Changing Admin Credentials

Edit `data/admin.txt`:
```
# Admin Credentials
# Format: USERNAME:PASSWORD
newadmin:newpassword123
```

### Customizing UI Colors

Edit `VotingSystemApp.java` constants:
```java
public static final Color PRIMARY_COLOR = new Color(41, 84, 144);
public static final Color SECONDARY_COLOR = new Color(247, 247, 247);
public static final Color ACCENT_COLOR = new Color(220, 53, 69);
public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
```

## 🛠️ Troubleshooting

### Common Issues

**Q: "Class not found" error when running**
A: Ensure you've compiled all Java files and are running from the correct directory.

**Q: "Permission denied" when writing files**
A: Check that the application has write permissions in the data directory.

**Q: "Invalid Aadhaar ID" error**
A: Ensure the Aadhaar ID is exactly 8 digits and is pre-registered in the system.

**Q: Application window doesn't appear**
A: Check if you have Java GUI libraries installed. Try running with `-Djava.awt.headless=false`

### Debug Mode

Run with debug information:
```bash
java -Djava.awt.debug=true -cp ./src main.VotingSystemApp
```

### Reset System Data

To start fresh:
1. Stop the application
2. Delete the entire `data/` directory
3. Restart the application (files will be recreated)

## 🔒 Security Features

- **Multi-layer Authentication**: Admin and voter verification
- **Input Validation**: All inputs sanitized and validated
- **Session Security**: Automatic lockout after failed attempts
- **Data Encryption**: Sensitive data protected with encryption
- **Audit Trail**: Complete logging of all system activities
- **Anti-tampering**: Vote integrity checks and validation
- **Anonymous Voting**: No traceability between voters and votes

## 📊 Data Export

The system can export results to CSV:
```java
// Results are automatically formatted and can be exported
// Check activity logs for detailed voting statistics
```

## 🤝 Contributing

This is an educational project. To extend functionality:

1. **Adding New Features**
   - Extend the appropriate manager classes
   - Update the GUI panels as needed
   - Add proper validation and security checks

2. **UI Improvements**
   - Modify `VotingPanels.java` for layout changes
   - Update color schemes and fonts in constants

3. **Security Enhancements**
   - Extend `SecurityManager.java` for additional protections
   - Implement stronger encryption algorithms

## 📋 System Requirements

### Minimum Requirements
- **OS**: Windows 7/macOS 10.12/Linux Ubuntu 16.04 or later
- **Java**: JDK 8 or OpenJDK 8
- **RAM**: 2GB minimum
- **Storage**: 50MB for application + data

### Recommended Requirements
- **OS**: Windows 10/macOS 10.15/Linux Ubuntu 20.04 or later
- **Java**: JDK 11 or later
- **RAM**: 4GB or more
- **Storage**: 100MB for application + logs + exports

## 🎓 Educational Use

This system is designed for:
- **Computer Science Projects**: Demonstrates GUI development, file I/O, security
- **Software Engineering**: Shows design patterns, modular architecture
- **Database Concepts**: File-based data management and integrity
- **Security Studies**: Authentication, encryption, and audit trails
- **Election Studies**: Understanding electronic voting processes

## ⚖️ Legal Notice

This software is for educational and demonstration purposes only. It is not intended for actual election use without proper security audits, certifications, and legal compliance validation.

## 📞 Support

For technical issues or questions:
1. Check this README and SECURITY.md documentation
2. Review the source code comments for implementation details
3. Check the activity logs in `data/activity.log` for system events

---

**Digital Voting System v1.0**  
*Secure • Professional • Educational*
