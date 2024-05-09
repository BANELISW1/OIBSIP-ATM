package src.com.example;
import java.util.ArrayList;
import java.util.List;

public class User<AccountType> {
    private String userId;
    private String pin;
    private double balance;
    private List<String> transactionHistory;
    private AccountType account;

    // Constructor with parameters for userId, pin, and initial balance
    public User(String userId, String pin, double initialBalance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    // Constructor with parameters for userId and pin
    public User(String userId, String pin) {
        this.userId = userId;
        this.pin = pin;
        this.transactionHistory = new ArrayList<>();
    }


    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void updateBalance(double amount) {
        balance += amount;
    }

    public double getBalance() {
        return balance;
    }

    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            System.out.println("Invalid amount for deposit.");
        }
    }

    public AccountType getAccount() {
        return account;
    }

    public void setAccount(AccountType account) {
        this.account = account;
    }
}
