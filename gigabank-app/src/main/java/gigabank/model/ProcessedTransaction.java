package gigabank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "processed_transactions", schema = "banking")
public class ProcessedTransaction {
    @Id
    @Column(name = "transaction_id")
    private UUID transactionId;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
