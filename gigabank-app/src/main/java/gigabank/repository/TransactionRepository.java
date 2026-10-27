package gigabank.repository;

import gigabank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> getTransactionsByAccount_AccountNumber(String accountAccountNumber);
}
