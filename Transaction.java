import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private LocalDateTime timestamp;
    private TransactionType type;
    private double amount;

    public Transaction(String transactionId, TransactionType type, double amount) {
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}

