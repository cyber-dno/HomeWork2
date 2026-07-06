package service.db;

import entity.BankAccount;
import entity.Transaction;
import entity.User;
import classifier.TransactionType;

import java.util.stream.Stream;

public interface TransactionService {
    Stream<Transaction> getTransactionsByAccount(BankAccount account);

    Stream<Transaction> getTransactionsByAccountAndType(BankAccount account, TransactionType type);

    Stream<Transaction> getTransactionsByUser(User user);

    Stream<Transaction> getTransactionsByUserAndType(User user, TransactionType type);
}
