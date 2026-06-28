package Entity;

import Enum.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Transaction {
    private String id;
    private BigDecimal value;
    private TransactionType type;
    private String category;
    private BankAccount bankAccount;
    private LocalDateTime createdDate;
    private String merchantName;
    private String merchantCategoryCode;
    private String cardNumber;
    private String bankName;
    private String digitalWalletId;

    public Transaction(String id, BigDecimal value, TransactionType type, String category, LocalDateTime createdDate) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.category = category;
        this.createdDate = createdDate;
    }
}
