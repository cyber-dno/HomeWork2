package service.db;

import entity.BankAccount;
import entity.Transaction;
import entity.User;
import classifier.TransactionType;

import java.sql.Connection;
import java.util.stream.Stream;

public interface TransactionService {
    Stream<Transaction> getTransactionsByAccount(BankAccount account, Connection connection);

    Stream<Transaction> getTransactionsByAccountAndType(BankAccount account, TransactionType type, Connection connection);

    Stream<Transaction> getTransactionsByUser(User user, Connection connection);

    Stream<Transaction> getTransactionsByUserAndType(User user, TransactionType type, Connection connection);
}
