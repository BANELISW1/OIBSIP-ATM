import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.util.Scanner;
import src.com.example.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.List;
import java.awt.event.*;

public class ATMGUI<ATMInterface> extends JFrame {
    protected static final JTextComponent amountField = null;
    private ATM atm;
    private JTextField userIdField;
    private JPasswordField pinField;
    private User currentUser;
    private ATMGUI atmGUI;

    public ATMGUI(ATM atm) {
        this.atm = atm;
        this.atmGUI = this; // Initialize atmGUI
        initComponents();
    }

    private void initComponents() {
        setTitle("ATM Simulator");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 5, 5));

        panel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        panel.add(userIdField);

        panel.add(new JLabel("PIN:"));
        pinField = new JPasswordField();
        panel.add(pinField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());
        panel.add(loginButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ExitButtonListener());
        panel.add(exitButton);

        add(panel);
        setVisible(true);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String userId = userIdField.getText();
            String pin = new String(pinField.getPassword());
    
            User currentUser = authenticateUser(userId, pin);
            if (currentUser != null) {
                // Close the login window
                dispose();
                // Set the currentUser field
                atmGUI.setCurrentUser(currentUser);
                // Show the ATM interface
                showATMInterface(userId);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid user ID or PIN.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateInput(String userId, String pin) {
        return userId.length() >= 6 && userId.length() <= 12 && pin.length() == 4;
    }

    private User authenticateUser(String userId, String pin) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1")) {
            String sql = "SELECT * FROM users WHERE user_id = ? AND pin = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, userId);
                statement.setString(2, pin);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return new User(userId, pin); // Return authenticated user object
                    } else {
                        return null; // User authentication failed
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            return null;
        }
    }
    private void showATMInterface(String userId) {
        JFrame atmFrame = new JFrame("ATM Interface");
        atmFrame.setSize(600, 400);
        atmFrame.setLocationRelativeTo(null);
        atmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JPanel panel = new JPanel(new BorderLayout());
    
        JLabel userInfoLabel = new JLabel("Welcome, " + userId + "!");
        panel.add(userInfoLabel, BorderLayout.NORTH);
    
        // Create buttons for menu options
        JButton viewTransactionHistoryButton = new JButton("View Transaction History");
        JButton withdrawButton = new JButton("Withdraw");
        JButton depositButton = new JButton("Deposit");
        JButton quitButton = new JButton("Quit");
    
        // Assign action listeners to buttons
        viewTransactionHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTransactionHistory(userId, null);
            }
        });
    
        atmFrame.setContentPane(panel);
        atmFrame.setVisible(true);

    withdrawButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // Handle withdrawal action
            if (currentUser != null) {
                double amount = getDoubleInput("Enter the amount to withdraw:");
                atm.withdraw(currentUser, amount);
            } else {
                JOptionPane.showMessageDialog(null, "Error: Current user is null.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
    
    depositButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // Handle deposit action
            if (currentUser != null) {
                double amount = getDoubleInput("Enter the amount to deposit:");
                atm.deposit(currentUser, amount);
            } else {
                JOptionPane.showMessageDialog(null, "Error: Current user is null.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
    
    quitButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            // Handle quit action
            System.out.println("Thank you for using the ATM. Goodbye!");
            atmFrame.dispose();
        }
    });

        // Add buttons to the panel
        panel.add(viewTransactionHistoryButton, BorderLayout.WEST);
        panel.add(withdrawButton, BorderLayout.CENTER);
        panel.add(depositButton, BorderLayout.EAST);
        panel.add(quitButton, BorderLayout.SOUTH);
    
        atmFrame.setContentPane(panel);
        atmFrame.setVisible(true);
    }


        
    private void displayTransactionHistory(String userId, JTextArea transactionHistoryArea) {
        StringBuilder history = new StringBuilder();
        // Fetch transaction history from the database and append it to the StringBuilder
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions WHERE user_id = ?");
        ) {
            statement.setString(1, userId);
            try (ResultSet transactions = statement.executeQuery()) {
                while (transactions.next()) {
                    int transactionId = transactions.getInt("transaction_id");
                    String type = transactions.getString("transaction_type");
                    double amount = transactions.getDouble("amount");
                    history.append("Transaction ID: ").append(transactionId).append("\n");
                    history.append("Type: ").append(type).append("\n");
                    history.append("Amount: ").append(amount).append("\n");
                    history.append("-----------------------------\n");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching transaction history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    
        // Set the transaction history text in the JTextArea
        transactionHistoryArea.setText(history.toString());
    }
    
    
    

    private void displayTransactionHistory(ResultSet transactions2) {
        JFrame historyFrame = new JFrame("Transaction History");
        historyFrame.setSize(400, 300);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
    
        StringBuilder history = new StringBuilder();
        // Fetch transaction history from the database and append it to the StringBuilder
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1");
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions WHERE user_id = ?");
        ) {
            statement.setString(1, currentUser.getUserId());
            try (ResultSet transactions = statement.executeQuery()) {
                while (transactions.next()) {
                    int transactionId = transactions.getInt("transaction_id");
                    String type = transactions.getString("transaction_type");
                    double amount = transactions.getDouble("amount");
                    history.append("Transaction ID: ").append(transactionId).append("\n");
                    history.append("Type: ").append(type).append("\n");
                    history.append("Amount: ").append(amount).append("\n");
                    history.append("-----------------------------\n");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error fetching transaction history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    
        textArea.setText(history.toString());
        historyFrame.add(scrollPane);
        historyFrame.setVisible(true);
    }
    

    private void displayMenu(Object currentUser) {
        if (currentUser == null) {
            System.out.println("Current user is null. Cannot proceed.");
            return;
        }
    
        System.out.println("\nWelcome, " + currentUser + "! Please select an option:");
        System.out.println("1. View Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Quit");
    
        int choice = getIntInput("Enter your choice:");
        switch (choice) {
            case 1:
                try {
                    ResultSet transactions = atm.getTransactionHistory(getTitle());
                    // Display transaction history
                    displayTransactionHistory(transactions);
                } catch (SQLException e) {
                    System.out.println("Error retrieving transaction history: " + e.getMessage());
                }
                break;
            case 2:
                // Prompt the user to enter the amount to withdraw
                double withdrawAmount = getDoubleInput("Enter the amount to withdraw:");
                // Withdraw the specified amount
                atm.withdraw(withdrawAmount);
                break;
            case 3:
                // Prompt the user to enter the amount to deposit
                double depositAmount = getDoubleInput("Enter the amount to deposit:");
                // Deposit the specified amount
                atm.deposit(depositAmount);
                break;
            case 4:
                System.out.println("Thank you for using the ATM. Goodbye!");
                System.exit(0); // Terminate the program
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }
        

    private int getIntInput(String prompt) {
        int value = 0;
        boolean isValidInput = false;
    
        while (!isValidInput) {
            try {
                // Prompt the user for input using JOptionPane
                String input = JOptionPane.showInputDialog(null, prompt, "Enter an Integer", JOptionPane.QUESTION_MESSAGE);
                
                // Convert the input to an integer
                value = Integer.parseInt(input);
                isValidInput = true;
            } catch (NumberFormatException e) {
                // If input cannot be parsed as an integer, show an error message
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    
        return value;
    }
    

    private double getDoubleInput(String prompt) {
        System.out.println(prompt);
        double value = 0.0;
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                value = Double.parseDouble(showInputDialog(prompt));
                isValidInput = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return value;
    }

    private String showInputDialog(String prompt) {
        JTextArea textArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(true);
        int result = JOptionPane.showOptionDialog(null, scrollPane, prompt, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (result == JOptionPane.OK_OPTION) {
            return textArea.getText();
        }
        return "";
    }

    public void start() {
        // Display a welcome message or any initial instructions
        JOptionPane.showMessageDialog(null, "Welcome to the ATM!", "Welcome", JOptionPane.INFORMATION_MESSAGE);

        // Loop to continue ATM operations until the user chooses to exit
        while (true) {
            // Authenticate the user
            Object currentUser = authenticateUser();

            // If authentication is successful, display the main menu
            if (currentUser != null) {
                displayMenu(currentUser);

                // Check if the user wants to perform another transaction
                if (!performAnotherTransaction()) {
                    break; // Exit the loop if the user chooses to quit
                }
            }
        }

        // Display a goodbye message
        JOptionPane.showMessageDialog(null, "Thank you for using the ATM. Goodbye!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
    }

    private Object authenticateUser() {
        // Prompt the user for their user ID and PIN
        String userId = showInputDialog("Enter your user ID:");
        String pin = showInputDialog("Enter your PIN:");

        try {
            // Establish connection to your database (assuming 'connection' is a valid Connection object)
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1");

            // Prepare SQL statement to query the database
            String sql = "SELECT * FROM users WHERE user_id = ? AND pin = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setString(2, pin);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Check if the query returned any rows
            if (resultSet.next()) {
                // If user exists and PIN is correct, return the authenticated user object
                return new User(userId, pin);
            } else {
                // If user doesn't exist or PIN is incorrect, return null
                JOptionPane.showMessageDialog(null, "Invalid user ID or PIN.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (SQLException e) {
            // Handle database connection or query errors
            System.out.println("Error authenticating user: " + e.getMessage());
            return null;
        }
    }

    private void handleWithdrawal() {
        String amountStr = JOptionPane.showInputDialog(null, "Enter the amount to withdraw:", "Withdrawal", JOptionPane.QUESTION_MESSAGE);
        try {
            double amount = Double.parseDouble(amountStr);
            atm.withdraw(currentUser, amount);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeposit() {
        String amountStr = JOptionPane.showInputDialog(null, "Enter the amount to deposit:", "Deposit", JOptionPane.QUESTION_MESSAGE);
        try {
            double amount = Double.parseDouble(amountStr);
            atm.deposit(currentUser, amount);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleQuit() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, "Thank you for using the ATM. Goodbye!", "Goodbye", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    
    

    private boolean performAnotherTransaction() {
        int result = JOptionPane.showConfirmDialog(null, "Would you like to perform another transaction?", "Another Transaction", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    private class ExitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATMGUI atmGUI = new ATMGUI(new ATM());
        });

    }
}