package gigabank.service.kafka;

import gigabank.classifier.TransactionType;
import gigabank.dto.TransactionDto;
import gigabank.model.BankAccount;
import gigabank.model.ProcessedTransaction;
import gigabank.repository.BankAccountRepository;
import gigabank.repository.ProcessedTransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProcessingService {
    private final ProcessedTransactionRepository processedTransactionRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public boolean processIdempotent(TransactionDto transactionDto) {
        if (processedTransactionRepository.existsById(transactionDto.getTransactionId())) {
            log.info("Duplicate detected, skipping: {}", transactionDto.getTransactionId());
            return false;
        }
        applyToBalance(transactionDto);

        ProcessedTransaction processed = new ProcessedTransaction();
        processed.setTransactionId(transactionDto.getTransactionId());
        processed.setAccountId(transactionDto.getAccountId());
        processed.setProcessedAt(Instant.now());
        processedTransactionRepository.save(processed);

        return true;
    }

    @Transactional
    public void process(TransactionDto transactionDto) {applyToBalance(transactionDto);}

    private void applyToBalance(TransactionDto transactionDto) {
        BankAccount account = bankAccountRepository
                .findByAccountNumber(transactionDto.getAccountId())
                .orElseThrow(() -> new IllegalStateException(
                        "Account not found: " + transactionDto.getAccountId()));

        BigDecimal delta = transactionDto.getType() == TransactionType.DEPOSIT
                ? transactionDto.getAmount()
                : transactionDto.getAmount().negate();

        BigDecimal newBalance = account.getBalance().add(delta);
        if (newBalance.signum() < 0) {
            throw new IllegalStateException(
                    "Insufficient funds for account " + transactionDto.getAccountId()
                            + ": balance=" + account.getBalance() + ", delta=" + delta);
        }

        account.setBalance(newBalance);
        bankAccountRepository.save(account);
    }
}
