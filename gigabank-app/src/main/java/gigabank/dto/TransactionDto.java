package gigabank.dto;

import gigabank.classifier.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private UUID transactionId;
    private String accountId;
    private TransactionType type;
    private BigDecimal amount;
    private Long createdAt;

    public static TransactionDto randomTransaction(String accountId) {
        return new TransactionDto(
                UUID.randomUUID(),
                accountId,
                Math.random() < 0.5 ? TransactionType.DEPOSIT : TransactionType.PAYMENT,
                BigDecimal.valueOf(Math.random() * 100000),
                System.currentTimeMillis()
        );
    }
}
