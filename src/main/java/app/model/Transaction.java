package app.model;

import java.time.LocalDateTime;

public class Transaction {
    private String cardNumber;
    private double amount;
    private String transactionType;
    private LocalDateTime timestamp;

    public Transaction(String cardNumber, double amount, String transactionType, LocalDateTime timestamp) {
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
