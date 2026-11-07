package gigabank.service.entity.transaction;

import gigabank.model.Transaction;
import gigabank.repository.TransactionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Cacheable(value = "transactionsCache", key = "'account:' + #accountId")
    public List<Transaction> getAccountTransactions(Long accountId) {
        return transactionRepository.getTransactionsByAccount_Id(accountId);
    }

    @Override
    @Cacheable(value = "transactionsCache", key = "'number:' + #number")
    public List<Transaction> getAccountTransactions(String number) {
        return transactionRepository.getTransactionsByAccount_AccountNumber(number);
    }

    @Override
    @Cacheable(value = "transactionsCache", key = "'id:' + #id", unless = "#result == null")
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "transactionsCache", key = "'account:' + #result.account.id"),
            @CacheEvict(value = "transactionsCache", key = "'number:' + #result.account.accountNumber"),
            @CacheEvict(value = "accountsCache", key = "'number:' + #result.account.accountNumber")
    })
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
