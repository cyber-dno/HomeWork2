package gigabank.service.entity.transaction;

import gigabank.model.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getAccountTransactions(Long accountId);

    List<Transaction> getAccountTransactions(String number);

    Transaction getTransactionById(Long id);

    Transaction createTransaction(Transaction transaction);
}
