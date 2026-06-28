package Service;

import Entity.BankAccount;
import Entity.Transaction;
import Entity.User;
import Enum.TransactionType;

import java.util.stream.Stream;

public interface TransactionService {
    Stream<Transaction> getTransactionsByAccount(BankAccount account);

    Stream<Transaction> getTransactionsByAccountAndType(BankAccount account, TransactionType type);

    Stream<Transaction> getTransactionsByUser(User user);

    Stream<Transaction> getTransactionsByUserAndType(User user, TransactionType type);
}
