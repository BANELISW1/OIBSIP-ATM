import javax.swing.*;

import src.com.example.User;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.PreparedStatement;
import java.util.Scanner;

import javax.swing.SwingUtilities;



public class ATM{
    private Scanner scanner;
    private Connection connection;
    private User currentUser;
    private ATMGUI atmGUI;

    public ATM() {
        this.scanner = new Scanner(System.in);
        try {
            // Establish a connection to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
    }

    public void start() {
        // Create and display the GUI
        SwingUtilities.invokeLater(() -> {
            atmGUI = new ATMGUI(this); // Pass 'this' as a reference to the ATM instance
            atmGUI.setVisible(true); // Correctly call setVisible on atmGUI
        });
    }
    
    public boolean authenticateUser(String userId, String pin) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE user_id = ? AND pin = ?")) {
            statement.setString(1, userId);
            statement.setString(2, pin);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    currentUser = new User(resultSet.getString("user_id"), resultSet.getString("pin"),
                            resultSet.getDouble("balance"));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error validating credentials: " + e.getMessage());
            return false;
        }
    }

    public void deposit(double amount) {
        if (amount > 0) {
            updateBalance(amount); // Update balance in the database
            insertTransaction("deposit", amount); // Insert transaction record
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && amount <= currentUser.getBalance()) {
            updateBalance(-amount); // Update balance in the database
            insertTransaction("withdrawal", amount); // Insert transaction record
        }
    }

    private void insertTransaction(String transactionType, double amount) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO transactions (user_id, transaction_type, amount, timestamp) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, currentUser.getUserId());
            statement.setString(2, transactionType);
            statement.setDouble(3, amount);
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting transaction record: " + e.getMessage());
        }
    }

    private void updateBalance(double amount) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE user_id = ?")) {
            statement.setDouble(1, amount);
            statement.setString(2, currentUser.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setATMGUI(ATMGUI atmGUI) {
        this.atmGUI = atmGUI;
    }

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.start();
    }

    public ResultSet getTransactionHistory(String userId) throws SQLException {
        // Define a Connection object for database connection
        Connection connection = null;
        // Define a PreparedStatement object for executing SQL queries
        PreparedStatement statement = null;
        // Define a ResultSet object for storing the query result
        ResultSet resultSet = null;

        try {
            // Establish a database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1");

            // Define the SQL query to retrieve transaction history for the given user ID
            String sql = "SELECT * FROM transactions WHERE user_id = ?";

            // Create a PreparedStatement object and set the user ID parameter
            statement = connection.prepareStatement(sql);
            statement.setString(1, userId);

            // Execute the query and store the result in the ResultSet object
            resultSet = statement.executeQuery();

            // Return the ResultSet object containing the transaction history
            return resultSet;
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
            throw e; // Rethrow the exception to be handled by the caller
        } finally {
            // Close the ResultSet, PreparedStatement, and Connection objects in the finally block
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

  }

  public void withdraw(User currentUser, double amount) {
    // Check if the user's balance is sufficient for the withdrawal
    if (currentUser.getBalance() >= amount) {
        // Update the user's balance in the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1");
             PreparedStatement withdrawStatement = connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE user_id = ?");
             PreparedStatement transactionStatement = connection.prepareStatement("INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, ?, ?)");
        ) {
            connection.setAutoCommit(false); // Start transaction

            // Deduct the amount from the user's balance
            withdrawStatement.setDouble(1, amount);
            withdrawStatement.setString(2, currentUser.getUserId());
            withdrawStatement.executeUpdate();

            // Record the withdrawal transaction
            transactionStatement.setString(1, currentUser.getUserId());
            transactionStatement.setString(2, "withdrawal");
            transactionStatement.setDouble(3, amount);
            transactionStatement.executeUpdate();

            connection.commit(); // Commit transaction
        } catch (SQLException ex) {
            System.out.println("Error withdrawing funds: " + ex.getMessage());
            ex.printStackTrace();
        }
    } else {
        System.out.println("Insufficient funds.");
    }
}

public void deposit(User currentUser, double amount) {
    // Update the user's balance in the database
    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_database", "root", "B@neliswa1");
         PreparedStatement depositStatement = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE user_id = ?");
         PreparedStatement transactionStatement = connection.prepareStatement("INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, ?, ?)");
    ) {
        connection.setAutoCommit(false); // Start transaction

        // Add the amount to the user's balance
        depositStatement.setDouble(1, amount);
        depositStatement.setString(2, currentUser.getUserId());
        depositStatement.executeUpdate();

        // Record the deposit transaction
        transactionStatement.setString(1, currentUser.getUserId());
        transactionStatement.setString(2, "deposit");
        transactionStatement.setDouble(3, amount);
        transactionStatement.executeUpdate();

        connection.commit(); // Commit transaction
    } catch (SQLException ex) {
        System.out.println("Error depositing funds: " + ex.getMessage());
        ex.printStackTrace();
    }
}

}
    