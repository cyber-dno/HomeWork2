package entity;

import classifier.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private String id;
    private BigDecimal value;
    private TransactionType type;
    private String category;
    private BankAccount bankAccount;
    private LocalDateTime createdDate;

    public Transaction(String id, BigDecimal value, TransactionType type, String category, LocalDateTime createdDate) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.category = category;
        this.createdDate = createdDate;
    }
}
