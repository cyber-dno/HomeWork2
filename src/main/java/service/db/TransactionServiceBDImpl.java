package service.db;

import classifier.TransactionType;
import db.DBManager;
import entity.BankAccount;
import entity.Transaction;
import entity.User;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TransactionServiceBDImpl implements TransactionService {
    private final DBManager dbManager = DBManager.getInstance();

    // Получение транзакций по счёту. Если счёта нет, то возвращается пустой список
    public Stream<Transaction> getTransactionsByAccount(BankAccount account, Connection connection) {
        if (account == null) {
            return Stream.empty();
        }
        return dbManager.getTransactionsByBankAccount(account, connection).stream();
    }

    // Получение транзакций по счёту по указанным категориям. Получаем список транзакций и фильтруем их по типу
    public Stream<Transaction> getTransactionsByAccountAndType(BankAccount account, TransactionType type, Connection connection) {
        return getTransactionsByAccount(account, connection)
                .filter(transaction -> transaction.getType() == type);
    }

    // Получение транзакций по клиенту. Если клиента нет, то возвращается пустой список
    public Stream<Transaction> getTransactionsByUser(User user, Connection connection) {
        if (user == null) {
            return Stream.empty();
        }
        return dbManager.getTransactionsByUser(user, connection).stream();
    }

    // Получение по клиенту по указанным категориям. Получаем список транзакций и фильтруем их по типу
    public Stream<Transaction> getTransactionsByUserAndType(User user, TransactionType type, Connection connection) {
        return getTransactionsByUser(user, connection)
                .filter(transaction -> transaction.getType() == type);
    }

    // Список транзакций, удовлетворяющих условию
    public List<Transaction> filterTransactions(User user, Predicate<Transaction> predicate, Connection connection) {
        return getTransactionsByUser(user, connection).filter(predicate).collect(Collectors.toList());
    }

    // Список строковых представлений транзакций
    public List<String> transformTransactions(User user, Function<Transaction, String> function, Connection connection) {
        return getTransactionsByUser(user, connection).map(function).collect(Collectors.toList());
    }

    // Применение функции к каждой транзакции
    public void processTransactions(User user, Consumer<Transaction> consumer, Connection connection) {
        getTransactionsByUser(user, connection).forEach(consumer);
    }

    // Возвращает созданный список транзакций
    public List<Transaction> createTransactionList(Supplier<List<Transaction>> supplier) {
        if (supplier == null)
            return new ArrayList<>();

        return supplier.get();
    }

    // Возвращает объединённый список транзакций
    public List<Transaction> mergeTransactionLists(
            List<Transaction> firstTransaction,
            List<Transaction> secondTransaction,
            BiFunction<List<Transaction>, List<Transaction>, List<Transaction>> biFunction
    ) {
        if (biFunction == null)
            return new ArrayList<>();

        return biFunction.apply(firstTransaction, secondTransaction);
    }
}