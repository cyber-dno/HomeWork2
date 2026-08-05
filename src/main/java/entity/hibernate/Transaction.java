package entity.hibernate;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transaction", schema = "banking")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account", referencedColumnName = "id", nullable = false)
    private BankAccount account;

    @Override
    public String toString() {
        return "Transaction{" +
                "description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", id=" + id +
                '}';
    }
}
