package entity;

import classifier.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String id;
    private User source;
    private User target;
    private User user;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDateTime date;

    public Transaction(User source, User target, User user, BigDecimal amount, TransactionType type, String category) {
        this.source = source;
        this.target = target;
        this.user = user;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }
}
