package gigabank.repository;

import gigabank.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount getBankAccountByAccountNumber(String accountNumber);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    BankAccount getBankAccountById(Long id);
}
