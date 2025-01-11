package com.mycompany.izibanking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

public class IziBanking {

    public static void main(String[] args) {

        // Initialize the database
        try {
            initializeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing the database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        createWalletsTable();

        // Create the main frame
        JFrame frame = new JFrame("Izi Banking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Create a panel for the header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(68, 51, 145)); // Purple background
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding for header

        // Add buttons to the header
        JButton createAccountButton = new JButton("Create account");
        createAccountButton.setBackground(new Color(255, 153, 0)); // Orange color
        createAccountButton.setForeground(Color.WHITE);

        // Attach this method to the "Create account" button
        createAccountButton.addActionListener(e -> openCreateAccountFrame());

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(68, 51, 145)); // Purple text

        headerPanel.add(createAccountButton);
        headerPanel.add(loginButton);

        // Add the header panel to the frame
        frame.add(headerPanel, BorderLayout.NORTH);

        // Create a panel for the main content
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(new Color(68, 51, 145)); // Purple background
        contentPanel.setLayout(new GridLayout(1, 2));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding for content

        // Create a panel for the left side text
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(68, 51, 145));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding for left panel

        JLabel title = new JLabel("Izi Banking System");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(new Color(255, 153, 0)); // Orange text

        JLabel subtitle = new JLabel("<html> IZI Banking is the ultimate digital banking companion, designed to simplify your financial journey. With a sleek interface and powerful features, managing your money has never been easier.</html>");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));

        leftPanel.add(Box.createVerticalStrut(50)); // Add spacing
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(20)); // Add spacing
        leftPanel.add(subtitle);
        subtitle.setForeground(Color.WHITE);

        // Add the left panel to the content
        contentPanel.add(leftPanel);

        // Add an image to the right panel
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding around the image

        // Load an image and set it as the icon
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\HP\\Downloads\\izi.png");
        Image image = imageIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(image));

        // Add the image to the content panel
        contentPanel.add(imageLabel);

        // Add the content panel to the frame
        frame.add(contentPanel, BorderLayout.CENTER);

        // Add an ActionListener to the Login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginFrame(); // Open a new JFrame
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }

    private static void initializeDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "")) {
            Statement stmt = conn.createStatement();

            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS IziBankingDB");

            // Close the connection and reconnect to the specific database
            conn.close();

            // Reconnect to the newly created database
            try (Connection dbConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                Statement dbStmt = dbConn.createStatement();

                // Create admin table
                String createAdminTable = """
                    CREATE TABLE IF NOT EXISTS admin (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) UNIQUE NOT NULL,
                        password VARCHAR(100) NOT NULL
                    )
                """;
                dbStmt.executeUpdate(createAdminTable);

                // Insert default admin credentials (if not exists)
                String insertAdmin = """
                    INSERT IGNORE INTO admin (username, password)
                    VALUES ('admin', 'admin123')
                """;
                dbStmt.executeUpdate(insertAdmin);

                // Create customer table
                String createCustomerTable = """
                    CREATE TABLE IF NOT EXISTS customer (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        first_name VARCHAR(200) NOT NULL,
                        last_name VARCHAR(200) NOT NULL,
                        account_number VARCHAR(200) NOT NULL,
                        total_balance double NULL,
                        national_id VARCHAR(200) NOT NULL UNIQUE,
                        phone VARCHAR(200) NOT NULL UNIQUE,
                        email VARCHAR(100) UNIQUE NOT NULL,
                        pin VARCHAR(6) NOT NULL
                    )
                """;
                dbStmt.executeUpdate(createCustomerTable);

                // Insert a sample customer
//                String insertCustomer = """
//                    INSERT IGNORE INTO customer (first_name, last_name, national_id, phone, email, pin)
//                    VALUES ('customer@gmail.com', 'customer123')
//                """;
//                dbStmt.executeUpdate(insertCustomer);
            }
        }
    }

    private static void createWalletsTable() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
            String createWalletsTableQuery = """
            CREATE TABLE IF NOT EXISTS wallets (
                wallet_id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                public_key TEXT NOT NULL,
                private_key TEXT NOT NULL,
                balance DOUBLE DEFAULT 0.0,
                FOREIGN KEY (user_id) REFERENCES customer(id)
            );
        """;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createWalletsTableQuery);
                System.out.println("Wallets table created or already exists.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating wallets table: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //open login options
    private static void openLoginFrame() {
    JFrame frame = new JFrame("Izi Banking System");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(900, 600);
    frame.setLayout(new BorderLayout());

    // Main content panel
    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(new Color(245, 245, 245)); // Light background
    contentPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0; // Position the components in the center horizontally
    gbc.gridy = 0; // Position the components at the top vertically
    gbc.insets = new Insets(10, 10, 10, 10); // Add padding around the components

    // Title label
    JLabel titleLabel = new JLabel("Sign In", JLabel.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

    // Admin button
    JButton adminButton = new JButton("Log In as Admin");
    adminButton.setBackground(new Color(68, 51, 145));
    adminButton.setForeground(Color.WHITE);

    // Customer button
    JButton customerButton = new JButton("Log In as Customer");
    customerButton.setBackground(new Color(68, 51, 145));
    customerButton.setForeground(Color.WHITE);

    // Add title label to content panel
    contentPanel.add(titleLabel, gbc);

    // Move down to next row for buttons
    gbc.gridy = 1;

    // Add admin button
    contentPanel.add(adminButton, gbc);

    // Move down to next row for customer button
    gbc.gridy = 2;

    // Add customer button
    contentPanel.add(customerButton, gbc);

    frame.add(contentPanel, BorderLayout.CENTER);

    // Add action listeners to buttons
    adminButton.addActionListener(e -> openAdminPage());
    customerButton.addActionListener(e -> openCustomerPage());

    // Make the frame visible
    frame.setVisible(true);
}

    private static void openAdminPage() {
        JFrame adminFrame = new JFrame("Admin Sign In");
        adminFrame.setSize(500, 400);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLayout(new BorderLayout());

        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new GridBagLayout());
        adminPanel.setBackground(new Color(248, 246, 252)); // Light background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Admin Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(68, 51, 145));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        adminPanel.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(new Color(68, 51, 145));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        adminPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        adminPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setForeground(new Color(68, 51, 145));
        gbc.gridx = 0;
        gbc.gridy = 2;
        adminPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        adminPanel.add(passwordField, gbc);

        JButton signInButton = new JButton("Sign In");
        signInButton.setBackground(new Color(68, 51, 145));
        signInButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        adminPanel.add(signInButton, gbc);

        adminFrame.add(adminPanel, BorderLayout.CENTER);

        signInButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(adminFrame, "Admin login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    adminFrame.dispose();
                    showAdminDashboard();//dashboard
                } else {
                    JOptionPane.showMessageDialog(adminFrame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(adminFrame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        adminFrame.setVisible(true);
    } //login for admin

    private static void openCustomerPage() { //login for customer
        JFrame customerFrame = new JFrame("Customer Sign In");
        customerFrame.setSize(500, 400);
        customerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerFrame.setLayout(new BorderLayout());

        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new GridBagLayout());
        customerPanel.setBackground(new Color(248, 246, 252)); // Light background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Customer Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(68, 51, 145));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        customerPanel.add(titleLabel, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(new Color(68, 51, 145));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        customerPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        customerPanel.add(emailField, gbc);

        JLabel pinLabel = new JLabel("Banking PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        pinLabel.setForeground(new Color(68, 51, 145));
        gbc.gridx = 0;
        gbc.gridy = 2;
        customerPanel.add(pinLabel, gbc);

        JPasswordField pinField = new JPasswordField();
        pinField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        customerPanel.add(pinField, gbc);

        JButton signInButton = new JButton("Sign In");
        signInButton.setBackground(new Color(68, 51, 145));
        signInButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        customerPanel.add(signInButton, gbc);

        customerFrame.add(customerPanel, BorderLayout.CENTER);

        signInButton.addActionListener(e -> {
            String email = emailField.getText();
            String pin = new String(pinField.getPassword());

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                String query = "SELECT * FROM customer WHERE email = ? AND pin = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, email);
                stmt.setString(2, pin);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(customerFrame, "Customer login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    customerFrame.dispose();
                    showCustomerDashboard(email);
                } else {
                    JOptionPane.showMessageDialog(customerFrame, "Invalid email or PIN.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(customerFrame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        customerFrame.setVisible(true);
    } //login for customer

private static void showAdminDashboard() {
    // Create the JFrame for the admin dashboard
    JFrame dashboard = new JFrame("Admin Dashboard");
    dashboard.setSize(800, 600);
    dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    dashboard.setLayout(new BorderLayout());

    // Header panel
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(68, 51, 145));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    JLabel headerLabel = new JLabel("Admin Dashboard", JLabel.CENTER);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
    headerLabel.setForeground(Color.WHITE);
    headerPanel.add(headerLabel, BorderLayout.CENTER);

    // Sidebar panel
    JPanel sidebarPanel = new JPanel(new GridLayout(4, 1, 5, 5)); // Changed 6 rows to 4 rows to remove "Reports" and "Settings"
    sidebarPanel.setBackground(new Color(248, 246, 252));
    sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    String[] menuItems = {"Manage Users", "Transactions", "Help", "Logout"}; // Removed "Reports" and "Settings"
    for (String item : menuItems) {
        JButton button = new JButton(item);
        button.setFont(new Font("Arial", Font.PLAIN, 12)); // Reduced font size
        button.setBackground(new Color(68, 51, 145));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        sidebarPanel.add(button);

        // Add action listeners for each button
        button.addActionListener(e -> {
            switch (item) {
                case "Manage Users":
                    showUserList();
                    break;
                case "Transactions":
                    showTransactions();  // Show transactions when the button is clicked
                    break;
                case "Help":
                    // Show a help dialog
                    JOptionPane.showMessageDialog(dashboard, 
                        "This is the Admin Dashboard.\n\n" +
                        "1. Manage Users: Allows you to manage user accounts.\n" +
                        "2. Transactions: View and manage transactions.\n" +
                        "3. Logout: Log out from the dashboard.", 
                        "Help", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "Logout":
                    // Confirm if admin wants to logout
                    int response = JOptionPane.showConfirmDialog(dashboard, "Are you sure you want to logout?", 
                                                                "Logout Confirmation", JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(dashboard, "Logged out successfully.");
                        // Add logout logic here, such as closing the dashboard or redirecting to a login screen
                        dashboard.dispose();  // Example: Close the dashboard window on logout
                    }
                    break;
            }
        });
    }

    // Main content panel
    JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
    mainPanel.setBackground(new Color(248, 246, 252));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Add example widgets/cards
    String[] stats = {"Total Users", "Total Transactions", "Total Wallets", "Revenue"};
    for (String stat : stats) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(68, 51, 145), 2));

        JLabel titleLabel = new JLabel(stat, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(68, 51, 145));

        JLabel valueLabel = new JLabel("0", JLabel.CENTER); // Placeholder for dynamic data
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(new Color(85, 85, 85));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        mainPanel.add(card);
    }

    // Add panels to the main dashboard layout
    dashboard.add(headerPanel, BorderLayout.NORTH);
    dashboard.add(sidebarPanel, BorderLayout.WEST);
    dashboard.add(mainPanel, BorderLayout.CENTER);

    // Display the dashboard
    dashboard.setVisible(true);
}
    private static void showTransactions() {
        // Create a new JFrame to show the transactions
        JFrame transactionsFrame = new JFrame("Transactions");
        transactionsFrame.setSize(800, 600);
        transactionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        transactionsFrame.setLayout(new BorderLayout());

        // Fetch the transaction data from the database
        Object[][] transactionData = fetchTransactionDataFromDatabase();
        String[] columnNames = {"Transaction ID", "Customer Name", "Amount", "Date"};

        // Create a JTable to display the transaction data
        JTable transactionsTable = new JTable(transactionData, columnNames);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);

        // Add the table to the frame
        transactionsFrame.add(scrollPane, BorderLayout.CENTER);

        // Show the transactions window
        transactionsFrame.setVisible(true);
    }

    private static Object[][] fetchTransactionDataFromDatabase() {
    // Query to get the transaction data (replace with your actual database query)
    String query = "SELECT transaction_id, id, amount, transaction_date FROM transactions";
    Object[][] data = new Object[0][0];

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "");
         Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); // Allow scrollable ResultSet
         ResultSet rs = stmt.executeQuery(query)) {

        // Get the row count
        rs.last();
        int rowCount = rs.getRow();
        rs.beforeFirst(); // Move cursor back to the beginning

        // Initialize the data array with the correct number of rows
        data = new Object[rowCount][4];

        int rowIndex = 0;
        while (rs.next()) {
            data[rowIndex][0] = rs.getInt("transaction_id");
            data[rowIndex][1] = rs.getString("id"); // Corrected column name, use the actual field for customer name
            data[rowIndex][2] = rs.getDouble("amount");
            data[rowIndex][3] = rs.getDate("transaction_date");
            rowIndex++;
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    return data;
}


// Method to display the user list
    private static void showUserList() {
        // Create a new JFrame to show the user list
        JFrame userListFrame = new JFrame("Manage Users");
        userListFrame.setSize(600, 400);
        userListFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create a panel to display the user list
        JPanel userListPanel = new JPanel(new BorderLayout());

        // Fetch users from the database and display them
        String[] columnNames = {"User ID", "Username", "Email"};
        Object[][] data = fetchUserDataFromDatabase(); // Method to fetch user data from database

        JTable userTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(userTable);
        userListPanel.add(scrollPane, BorderLayout.CENTER);

        // Add the user list panel to the frame
        userListFrame.add(userListPanel);

        // Show the user list window
        userListFrame.setVisible(true);
    }

// Method to fetch user data from the database
    private static Object[][] fetchUserDataFromDatabase() {
        // Example database query to get all users (replace with actual code to query your database)
        String query = "SELECT first_name, last_name, email FROM customer";
        Object[][] data = new Object[0][0];

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", ""); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(query)) {

            // Get the row count
            rs.last(); // Move to the last row
            int rowCount = rs.getRow(); // Get the row count
            rs.beforeFirst(); // Move the cursor back to the beginning

            // Initialize the data array with the correct number of rows
            data = new Object[rowCount][3];

            int rowIndex = 0;
            while (rs.next()) {
                // Corrected the data types and columns
                data[rowIndex][0] = rs.getString("first_name");
                data[rowIndex][1] = rs.getString("last_name");
                data[rowIndex][2] = rs.getString("email");
                rowIndex++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return data;
    }

    

    private static void showCustomerDashboard(String email) {
        JFrame dashboard = new JFrame("Customer Dashboard");
        dashboard.setSize(600, 600);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboard.setLayout(new BorderLayout());

        // Panel for welcoming the user
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(248, 246, 252));

        JLabel welcomeLabel = new JLabel("Welcome to the Customer Dashboard!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(68, 51, 145));
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);

        JLabel emailLabel = new JLabel("Logged in as: " + email, JLabel.CENTER);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(new Color(85, 85, 85));
        welcomePanel.add(emailLabel, BorderLayout.CENTER);

        // Panel for account details
        JPanel detailsPanel = new JPanel(new GridLayout(2, 1));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.setBackground(new Color(248, 246, 252));

        String accountNumber = ""; // To be fetched from the database
        double balance = 0.0; // To be fetched from the database
        String publicKey = ""; // Wallet public key to be fetched from the database
        double cryptoBalance = 0.0; // Cryptocurrency balance to be fetched from the database

        // Fetching account and wallet details
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
            // Fetch account details
            String accountQuery = "SELECT account_number, total_balance FROM customer WHERE email = ?";
            PreparedStatement accountStmt = conn.prepareStatement(accountQuery);
            accountStmt.setString(1, email);
            ResultSet accountRs = accountStmt.executeQuery();
            if (accountRs.next()) {
                accountNumber = accountRs.getString("account_number");
                balance = accountRs.getDouble("total_balance");
            }

            // Fetch wallet details
            String walletQuery = """
                SELECT public_key, balance 
                FROM wallets 
                INNER JOIN customer ON wallets.user_id = customer.id 
                WHERE customer.email = ?
                """;
            PreparedStatement walletStmt = conn.prepareStatement(walletQuery);
            walletStmt.setString(1, email);
            ResultSet walletRs = walletStmt.executeQuery();
            if (walletRs.next()) {
                publicKey = walletRs.getString("public_key");
                cryptoBalance = walletRs.getDouble("balance");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dashboard, "Error fetching account or wallet details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String finalPublicKey = publicKey; // Make publicKey effectively final
        String finalAccountNumber = accountNumber; // Make accountNumber effectively final

        JLabel accountNumberLabel = new JLabel("Account Number: " + accountNumber, JLabel.CENTER);
        accountNumberLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        accountNumberLabel.setForeground(new Color(68, 51, 145));

        JLabel balanceLabel = new JLabel("Total Balance: $" + String.format("%.2f", balance), JLabel.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        balanceLabel.setForeground(new Color(68, 51, 145));

        detailsPanel.add(accountNumberLabel);
        detailsPanel.add(balanceLabel);

        // Panel for cryptocurrency gateway
        JPanel cryptoPanel = new JPanel(new GridLayout(3, 1));
        cryptoPanel.setBorder(BorderFactory.createTitledBorder("Cryptocurrency Gateway"));
        cryptoPanel.setBackground(new Color(248, 246, 252));

        JLabel cryptoPublicKeyLabel = new JLabel("Wallet Public Key: " + publicKey, JLabel.CENTER);
        cryptoPublicKeyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cryptoPublicKeyLabel.setForeground(new Color(68, 51, 145));

        JLabel cryptoBalanceLabel = new JLabel("Cryptocurrency Balance: " + cryptoBalance + " BTC", JLabel.CENTER);
        cryptoBalanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cryptoBalanceLabel.setForeground(new Color(68, 51, 145));

        JButton manageWalletButton = new JButton("Manage Wallet");
        manageWalletButton.setBackground(new Color(68, 51, 145));
        manageWalletButton.setForeground(Color.WHITE);

        // Action listener for managing the wallet
        manageWalletButton.addActionListener(e -> openCryptoGatewayFrame(email, finalPublicKey));

        cryptoPanel.add(cryptoPublicKeyLabel);
        cryptoPanel.add(cryptoBalanceLabel);
        cryptoPanel.add(manageWalletButton);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(new Color(248, 246, 252));

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton statementButton = new JButton("Bank Statement");

        depositButton.setBackground(new Color(68, 51, 145));
        depositButton.setForeground(Color.WHITE);

        withdrawButton.setBackground(new Color(68, 51, 145));
        withdrawButton.setForeground(Color.WHITE);

        transferButton.setBackground(new Color(68, 51, 145));
        transferButton.setForeground(Color.WHITE);

        statementButton.setBackground(new Color(68, 51, 145));
        statementButton.setForeground(Color.WHITE);

        // Adding buttons to the panel
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(statementButton);

        // Adding action listeners for other buttons
        depositButton.addActionListener(e -> openDepositFrame(email, finalAccountNumber));
        withdrawButton.addActionListener(e -> openWithdrawFrame(email, finalAccountNumber));
        transferButton.addActionListener(e -> openTransferFrame(email, finalAccountNumber));
        statementButton.addActionListener(e -> openBankStatementFrame(email, finalAccountNumber));

        // Adding panels to the dashboard
        dashboard.add(welcomePanel, BorderLayout.NORTH);
        dashboard.add(detailsPanel, BorderLayout.CENTER);
        dashboard.add(cryptoPanel, BorderLayout.EAST);
        dashboard.add(buttonPanel, BorderLayout.SOUTH);

        dashboard.setVisible(true);
    }

    // Frame to manage cryptocurrency wallet
    private static void openCryptoGatewayFrame(String email, String publicKey) {
        // Create JFrame for Cryptocurrency Wallet
        JFrame cryptoFrame = new JFrame("Cryptocurrency Wallet");
        cryptoFrame.setSize(500, 500);  // Adjusted size to fit better
        cryptoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cryptoFrame.setLocationRelativeTo(null);  // Center the window

        // Create JPanel for the content inside the frame with purple background
        JPanel cryptoPanel = new JPanel();
        cryptoPanel.setLayout(new BoxLayout(cryptoPanel, BoxLayout.Y_AXIS));
        cryptoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cryptoPanel.setBackground(new Color(0x6A0DAD)); // Purple background color

        // Add label with wallet public key in white font
        JLabel walletInfoLabel = new JLabel("Public Key: " + publicKey, JLabel.CENTER);
        walletInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        walletInfoLabel.setForeground(Color.WHITE); // White text for the label
        walletInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center label

        // Create Buttons with purple background, white font, and custom styling
        JButton transactionButton = createPurpleButton("View Transactions");
        JButton balanceButton = createPurpleButton("Check Balance");
        JButton transferButton = createPurpleButton("Send Cryptocurrency");

        // Action listener for viewing transactions
        transactionButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                String query = """
                SELECT transaction_id, amount, transaction_type, transaction_date 
                FROM transactions 
                WHERE id = ?
                ORDER BY transaction_date DESC
                """;
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, publicKey);
                ResultSet rs = stmt.executeQuery();

                StringBuilder transactionDetails = new StringBuilder("<html><body style='width: 300px;'>");
                while (rs.next()) {
                    transactionDetails.append("<b>ID:</b> ").append(rs.getString("transaction_id")).append("<br>")
                            .append("<b>Type:</b> ").append(rs.getString("transaction_type")).append("<br>")
                            .append("<b>Amount:</b> ").append(rs.getDouble("amount")).append("<br>")
                            .append("<b>Time:</b> ").append(rs.getTimestamp("transaction_date")).append("<br><hr>");
                }
                transactionDetails.append("</body></html>");

                if (transactionDetails.toString().equals("<html><body style='width: 300px;'></body></html>")) {
                    transactionDetails = new StringBuilder("No transactions found.");
                }

                JOptionPane.showMessageDialog(cryptoFrame, transactionDetails.toString(), "Transactions", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(cryptoFrame, "Error fetching transactions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action listener for checking wallet balance
        balanceButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                String query = "SELECT balance FROM wallets WHERE public_key = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, publicKey);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    JOptionPane.showMessageDialog(cryptoFrame, "Your current balance is: " + balance + " BTC", "Wallet Balance", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(cryptoFrame, "Wallet not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(cryptoFrame, "Error fetching balance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action listener for initiating a cryptocurrency transfer
        transferButton.addActionListener(e -> {
            JTextField recipientField = new JTextField();
            JTextField amountField = new JTextField();

            Object[] message = {
                "Recipient Public Key:", recipientField,
                "Amount (BTC):", amountField
            };

            int option = JOptionPane.showConfirmDialog(cryptoFrame, message, "Send Cryptocurrency", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String recipientKey = recipientField.getText();
                double amount;

                try {
                    amount = Double.parseDouble(amountField.getText());

                    if (recipientKey.isEmpty() || amount <= 0) {
                        JOptionPane.showMessageDialog(cryptoFrame, "Invalid input. Please enter valid details.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Perform the transfer
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                        conn.setAutoCommit(false);

                        // Check sender's balance
                        String checkBalanceQuery = "SELECT balance FROM wallets WHERE public_key = ?";
                        PreparedStatement checkStmt = conn.prepareStatement(checkBalanceQuery);
                        checkStmt.setString(1, publicKey);
                        ResultSet checkRs = checkStmt.executeQuery();

                        if (checkRs.next()) {
                            double currentBalance = checkRs.getDouble("balance");
                            if (currentBalance < amount) {
                                JOptionPane.showMessageDialog(cryptoFrame, "Insufficient balance.", "Error", JOptionPane.ERROR_MESSAGE);
                                conn.rollback();
                                return;
                            }
                        } else {
                            JOptionPane.showMessageDialog(cryptoFrame, "Sender wallet not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            conn.rollback();
                            return;
                        }

                        // Deduct from sender
                        String deductQuery = "UPDATE wallets SET balance = balance - ? WHERE public_key = ?";
                        PreparedStatement deductStmt = conn.prepareStatement(deductQuery);
                        deductStmt.setDouble(1, amount);
                        deductStmt.setString(2, publicKey);
                        deductStmt.executeUpdate();

                        // Add to recipient
                        String addQuery = "UPDATE wallets SET balance = balance + ? WHERE public_key = ?";
                        PreparedStatement addStmt = conn.prepareStatement(addQuery);
                        addStmt.setDouble(1, amount);
                        addStmt.setString(2, recipientKey);
                        int rowsAffected = addStmt.executeUpdate();

                        if (rowsAffected == 0) {
                            JOptionPane.showMessageDialog(cryptoFrame, "Recipient wallet not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            conn.rollback();
                            return;
                        }

                        // Insert transaction record
                        String transactionQuery = """
                        INSERT INTO transactions (wallet_public_key, amount, transaction_type, timestamp) 
                        VALUES (?, ?, ?, NOW()), (?, ?, ?, NOW())
                        """;
                        PreparedStatement transactionStmt = conn.prepareStatement(transactionQuery);
                        transactionStmt.setString(1, publicKey);
                        transactionStmt.setDouble(2, -amount);
                        transactionStmt.setString(3, "Transfer Out");
                        transactionStmt.setString(4, recipientKey);
                        transactionStmt.setDouble(5, amount);
                        transactionStmt.setString(6, "Transfer In");
                        transactionStmt.executeUpdate();

                        conn.commit();
                        JOptionPane.showMessageDialog(cryptoFrame, "Transfer successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(cryptoFrame, "Error processing transfer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(cryptoFrame, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add all components to the panel
        cryptoPanel.add(walletInfoLabel);
        cryptoPanel.add(balanceButton);
        cryptoPanel.add(transactionButton);
        cryptoPanel.add(transferButton);

        // Scroll Pane for the panel
        JScrollPane scrollPane = new JScrollPane(cryptoPanel);
        cryptoFrame.add(scrollPane);

        cryptoFrame.setVisible(true);
    }

// Helper method to create a purple button with white text
    private static JButton createPurpleButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0x6A0DAD));  // Purple background
        button.setForeground(Color.WHITE);  // White text
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center align button
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));  // White border
        return button;
    }

    //create account frame
    private static void openCreateAccountFrame() {
        JFrame createAccountFrame = new JFrame("Create Account");
        createAccountFrame.setSize(500, 600);
        createAccountFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createAccountFrame.setLayout(new BorderLayout());

        JPanel createAccountPanel = new JPanel();
        createAccountPanel.setLayout(new GridBagLayout());
        createAccountPanel.setBackground(new Color(248, 246, 252)); // Light background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(68, 51, 145));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        createAccountPanel.add(titleLabel, gbc);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        createAccountPanel.add(firstNameLabel, gbc);

        JTextField firstNameField = new JTextField();
        firstNameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        createAccountPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        createAccountPanel.add(lastNameLabel, gbc);

        JTextField lastNameField = new JTextField();
        lastNameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        createAccountPanel.add(lastNameField, gbc);

        JLabel nationalIdLabel = new JLabel("National ID:");
        nationalIdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        createAccountPanel.add(nationalIdLabel, gbc);

        JTextField nationalIdField = new JTextField();
        nationalIdField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        createAccountPanel.add(nationalIdField, gbc);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        createAccountPanel.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField();
        phoneField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 4;
        createAccountPanel.add(phoneField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        createAccountPanel.add(emailLabel, gbc);

        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 5;
        createAccountPanel.add(emailField, gbc);

        JLabel pinLabel = new JLabel("Banking PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        createAccountPanel.add(pinLabel, gbc);

        JPasswordField pinField = new JPasswordField();
        pinField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 6;
        createAccountPanel.add(pinField, gbc);

        JButton submitButton = new JButton("Create Account");
        submitButton.setBackground(new Color(68, 51, 145));
        submitButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        createAccountPanel.add(submitButton, gbc);

        createAccountFrame.add(createAccountPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String nationalId = nationalIdField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String pin = new String(pinField.getPassword());

            // Validate input fields
            if (firstName.isEmpty() || lastName.isEmpty() || nationalId.isEmpty() || phone.isEmpty() || email.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(createAccountFrame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Generate a random account number
            String accountNumber = "IZI-" + (int) (Math.random() * 1000000);

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                // Save customer details
                String query = """
            INSERT INTO customer (first_name, last_name, national_id, phone, email, pin, account_number, total_balance)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, nationalId);
                stmt.setString(4, phone);
                stmt.setString(5, email);
                stmt.setString(6, pin);
                stmt.setString(7, accountNumber);
                stmt.setDouble(8, 0.0); // Default balance
                stmt.executeUpdate();

                // Get the generated customer ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);

                    // Generate wallet keys
                    String[] keys = generateWalletKeys();
                    String publicKey = keys[0];
                    String privateKey = keys[1];

                    // Save wallet details
                    String walletQuery = "INSERT INTO wallets (user_id, public_key, private_key, balance) VALUES (?, ?, ?, ?)";
                    PreparedStatement walletStmt = conn.prepareStatement(walletQuery);
                    walletStmt.setInt(1, userId);
                    walletStmt.setString(2, publicKey);
                    walletStmt.setString(3, privateKey);
                    walletStmt.setDouble(4, 0.0); // Initial balance
                    walletStmt.executeUpdate();

                    JOptionPane.showMessageDialog(createAccountFrame, "Account created successfully! \nYour account number is: " + accountNumber + "\nYour wallet public key is: " + publicKey, "Success", JOptionPane.INFORMATION_MESSAGE);
                    createAccountFrame.dispose();
                    openCustomerPage(); // Navigate to customer login frame
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(createAccountFrame, "Error creating account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        createAccountFrame.setVisible(true);
    }

    private static String[] generateWalletKeys() {
        try {
            // Generate random public and private key strings
            String publicKey = "IZI-RSA-PUK-" + (int) (Math.random() * 1000000000);
            String privateKey = "IZI-RSA-PRK-" + (int) (Math.random() * 1000000000);

            // Return both the public and private keys as a string array
            return new String[]{publicKey, privateKey};
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating wallet keys");
        }
    }

    private static void openDepositFrame(String email, String accountNumber) {
        JFrame depositFrame = new JFrame("Deposit");
        depositFrame.setSize(450, 300);
        depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        depositFrame.setLayout(new BorderLayout());

        // Colors and fonts
        Color primaryColor = new Color(68, 51, 145);
        Color secondaryColor = new Color(248, 246, 252);
        Font headingFont = new Font("Arial", Font.BOLD, 18);
        Font regularFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(primaryColor);
        JLabel headerLabel = new JLabel("Deposit Money", JLabel.CENTER);
        headerLabel.setFont(headingFont);
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBackground(secondaryColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel amountLabel = new JLabel("Amount to Deposit:");
        amountLabel.setFont(regularFont);
        amountLabel.setForeground(primaryColor);

        JTextField amountField = new JTextField();
        amountField.setFont(regularFont);
        amountField.setBorder(BorderFactory.createLineBorder(primaryColor));

        mainPanel.add(amountLabel);
        mainPanel.add(amountField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBackground(secondaryColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        // Button styling
        submitButton.setFont(buttonFont);
        submitButton.setBackground(primaryColor);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        cancelButton.setFont(buttonFont);
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // Adding panels to the frame
        depositFrame.add(headerPanel, BorderLayout.NORTH);
        depositFrame.add(mainPanel, BorderLayout.CENTER);
        depositFrame.add(buttonPanel, BorderLayout.SOUTH);

        depositFrame.setVisible(true);

        // Button Actions
        submitButton.addActionListener(e -> {
            String amountText = amountField.getText();
            try {
                double amount = Double.parseDouble(amountText);
                if (amount > 0) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                        // Step 1: Get the customer id for the given account_number
                        String getIdQuery = "SELECT id FROM customer WHERE account_number = ?";
                        PreparedStatement getIdStmt = conn.prepareStatement(getIdQuery);
                        getIdStmt.setString(1, accountNumber);
                        ResultSet rs = getIdStmt.executeQuery();
                        if (rs.next()) {
                            int customerId = rs.getInt("id");

                            // Step 2: Update the customer's total balance
                            String updateBalanceQuery = "UPDATE customer SET total_balance = total_balance + ? WHERE account_number = ?";
                            PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                            updateBalanceStmt.setDouble(1, amount);
                            updateBalanceStmt.setString(2, accountNumber);
                            updateBalanceStmt.executeUpdate();

                            // Step 3: Insert the deposit transaction into the transactions table
                            String insertTransactionQuery = "INSERT INTO transactions (id, transaction_type, amount) VALUES (?, 'Deposit', ?)";
                            PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionQuery);
                            insertTransactionStmt.setInt(1, customerId); // Use the customer id here
                            insertTransactionStmt.setDouble(2, amount);
                            insertTransactionStmt.executeUpdate();

                            JOptionPane.showMessageDialog(depositFrame, "Deposit Successful!");
                            depositFrame.dispose();
                            showCustomerDashboard(email); // Refresh the dashboard after deposit
                        } else {
                            JOptionPane.showMessageDialog(depositFrame, "Account number not found!", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(depositFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(depositFrame, "Enter a valid amount!", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(depositFrame, "Please enter a valid numeric amount.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> depositFrame.dispose());
    }

    private static void openWithdrawFrame(String email, String accountNumber) {
        // Create the JFrame for withdrawal
        JFrame withdrawFrame = new JFrame("Withdraw");
        withdrawFrame.setSize(400, 300);
        withdrawFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with grid layout for form
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(248, 246, 252)); // Light background color

        // Labels and fields
        JLabel amountLabel = new JLabel("Amount to Withdraw:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setForeground(new Color(68, 51, 145)); // Primary text color

        JTextField amountField = new JTextField();
        amountField.setBorder(BorderFactory.createLineBorder(new Color(68, 51, 145), 1));

        // Buttons
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        // Style buttons
        submitButton.setBackground(new Color(68, 51, 145));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(85, 85, 85));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        // Add components to the panel
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(submitButton);
        panel.add(cancelButton);

        // Add panel to the frame
        withdrawFrame.add(panel);
        withdrawFrame.setVisible(true);

        // Action listener for the submit button
        submitButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount > 0) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                        // Query to fetch account details
                        String query = "SELECT id, total_balance FROM customer WHERE account_number = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, accountNumber);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            int customerId = rs.getInt("id");
                            double currentBalance = rs.getDouble("total_balance");

                            if (currentBalance >= amount) {
                                // Update the balance
                                String updateBalanceQuery = "UPDATE customer SET total_balance = total_balance - ? WHERE account_number = ?";
                                PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                                updateBalanceStmt.setDouble(1, amount);
                                updateBalanceStmt.setString(2, accountNumber);
                                updateBalanceStmt.executeUpdate();

                                // Log the transaction
                                String insertTransactionQuery = "INSERT INTO transactions (id, transaction_type, amount) VALUES (?, 'Withdrawal', ?)";
                                PreparedStatement insertTransactionStmt = conn.prepareStatement(insertTransactionQuery);
                                insertTransactionStmt.setInt(1, customerId);
                                insertTransactionStmt.setDouble(2, amount);
                                insertTransactionStmt.executeUpdate();

                                JOptionPane.showMessageDialog(withdrawFrame, "Withdrawal Successful!");
                                withdrawFrame.dispose();
                                showCustomerDashboard(email); // Refresh dashboard
                            } else {
                                JOptionPane.showMessageDialog(withdrawFrame, "Insufficient Balance!", "Error", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(withdrawFrame, "Account number not found!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(withdrawFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(withdrawFrame, "Enter a valid amount!", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(withdrawFrame, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action listener for the cancel button
        cancelButton.addActionListener(e -> withdrawFrame.dispose());
    }

    private static void openTransferFrame(String email, String accountNumber) {
        // Create the JFrame for transfer
        JFrame transferFrame = new JFrame("Transfer");
        transferFrame.setSize(400, 300);
        transferFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with grid layout
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(248, 246, 252)); // Light background color

        // Labels and input fields
        JLabel toAccountLabel = new JLabel("To Account Number:");
        toAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toAccountLabel.setForeground(new Color(68, 51, 145)); // Primary text color

        JTextField toAccountField = new JTextField();
        toAccountField.setBorder(BorderFactory.createLineBorder(new Color(68, 51, 145), 1));

        JLabel amountLabel = new JLabel("Amount to Transfer:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        amountLabel.setForeground(new Color(68, 51, 145));

        JTextField amountField = new JTextField();
        amountField.setBorder(BorderFactory.createLineBorder(new Color(68, 51, 145), 1));

        // Buttons
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        // Style buttons
        submitButton.setBackground(new Color(68, 51, 145)); // App original color
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(255, 0, 0)); // Red color
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        // Add components to panel
        panel.add(toAccountLabel);
        panel.add(toAccountField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(submitButton);
        panel.add(cancelButton);

        // Add panel to frame
        transferFrame.add(panel);
        transferFrame.setVisible(true);

        // Action listener for submit button
        submitButton.addActionListener(e -> {
            String toAccount = toAccountField.getText();
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount > 0 && !toAccount.isEmpty()) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
                        conn.setAutoCommit(false);

                        // Check sender's balance
                        String balanceQuery = "SELECT id, total_balance FROM customer WHERE account_number = ?";
                        PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
                        balanceStmt.setString(1, accountNumber);
                        ResultSet balanceRs = balanceStmt.executeQuery();

                        if (balanceRs.next()) {
                            int senderId = balanceRs.getInt("id");
                            double senderBalance = balanceRs.getDouble("total_balance");

                            if (senderBalance >= amount) {
                                // Deduct from sender
                                String deductQuery = "UPDATE customer SET total_balance = total_balance - ? WHERE account_number = ?";
                                PreparedStatement deductStmt = conn.prepareStatement(deductQuery);
                                deductStmt.setDouble(1, amount);
                                deductStmt.setString(2, accountNumber);
                                deductStmt.executeUpdate();

                                // Add to receiver
                                String addQuery = "UPDATE customer SET total_balance = total_balance + ? WHERE account_number = ?";
                                PreparedStatement addStmt = conn.prepareStatement(addQuery);
                                addStmt.setDouble(1, amount);
                                addStmt.setString(2, toAccount);
                                int rowsUpdated = addStmt.executeUpdate();

                                if (rowsUpdated > 0) {
                                    // Insert sender's transaction
                                    String senderTransactionQuery = "INSERT INTO transactions (id, transaction_type, amount, recipient_account) VALUES (?, 'Transfer', ?, ?)";
                                    PreparedStatement senderTransactionStmt = conn.prepareStatement(senderTransactionQuery);
                                    senderTransactionStmt.setInt(1, senderId);
                                    senderTransactionStmt.setDouble(2, amount);
                                    senderTransactionStmt.setString(3, toAccount);
                                    senderTransactionStmt.executeUpdate();

                                    // Insert receiver's transaction
                                    String receiverTransactionQuery = "INSERT INTO transactions (id, transaction_type, amount, recipient_account) VALUES (?, 'Transfer', ?, ?)";
                                    PreparedStatement receiverTransactionStmt = conn.prepareStatement(receiverTransactionQuery);
                                    receiverTransactionStmt.setInt(1, getReceiverId(conn, toAccount)); // Fetch receiver's ID
                                    receiverTransactionStmt.setDouble(2, amount);
                                    receiverTransactionStmt.setString(3, accountNumber);
                                    receiverTransactionStmt.executeUpdate();

                                    conn.commit();
                                    JOptionPane.showMessageDialog(transferFrame, "Transfer Successful!");
                                    transferFrame.dispose();
                                    showCustomerDashboard(email);
                                } else {
                                    conn.rollback();
                                    JOptionPane.showMessageDialog(transferFrame, "Invalid Recipient Account Number!", "Error", JOptionPane.WARNING_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(transferFrame, "Insufficient Balance!", "Error", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(transferFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(transferFrame, "Enter valid details!", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(transferFrame, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action listener for cancel button
        cancelButton.addActionListener(e -> transferFrame.dispose());
    }

// Helper method to fetch receiver's customer ID
    private static int getReceiverId(Connection conn, String toAccount) throws SQLException {
        String query = "SELECT id FROM customer WHERE account_number = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, toAccount);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        return -1; // Return -1 if account not found
    }

    private static void openBankStatementFrame(String email, String accountNumber) {
        JFrame statementFrame = new JFrame("Bank Statement");
        statementFrame.setSize(500, 400);
        statementFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea statementArea = new JTextArea();
        statementArea.setEditable(false);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/IziBankingDB", "root", "")) {
            // Step 1: Retrieve the customer ID from the account number
            String getIdQuery = "SELECT id FROM customer WHERE account_number = ?";
            PreparedStatement getIdStmt = conn.prepareStatement(getIdQuery);
            getIdStmt.setString(1, accountNumber);
            ResultSet rs = getIdStmt.executeQuery();

            int customerId = -1;
            if (rs.next()) {
                customerId = rs.getInt("id");
            } else {
                statementArea.setText("No customer found with account number: " + accountNumber);
                statementFrame.add(new JScrollPane(statementArea));
                statementFrame.setVisible(true);
                return; // Exit if account number doesn't exist
            }

            // Step 2: Fetch transactions for the customer ID
            String query = "SELECT transaction_date, transaction_type, amount, recipient_account FROM transactions WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, customerId);  // Use customer id here instead of account number
            ResultSet transactionResults = stmt.executeQuery();

            StringBuilder statement = new StringBuilder("Bank Statement\n\n");
            statement.append(String.format("%-15s %-10s %-20s %-20s\n", "Type", "Amount", "Recipient", "Date"));
            statement.append("---------------------------------------------------------------\n");

            boolean hasTransactions = false;
            while (transactionResults.next()) {
                hasTransactions = true;
                statement.append(String.format("%-15s %-10.2f %-20s %-20s\n",
                        transactionResults.getString("transaction_type"),
                        transactionResults.getDouble("amount"),
                        transactionResults.getString("recipient_account") == null ? "N/A" : transactionResults.getString("recipient_account"),
                        transactionResults.getTimestamp("transaction_date")));
            }

            if (!hasTransactions) {
                statement.append("No transactions found for this account.");
            }

            statementArea.setText(statement.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            statementArea.setText("Error fetching bank statement: " + ex.getMessage());
        }

        statementFrame.add(new JScrollPane(statementArea));
        statementFrame.setVisible(true);
    }
}
