package Service;

import Entity.BankAccount;
import Entity.Transaction;
import Entity.User;
import Enum.TransactionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionService {
    // Получение транзакций по счёту. Если счёта нет, то возвращается пустой список
    public static Stream<Transaction> getTransactionsByAccount(BankAccount account) {
        if (account == null) {
            return Stream.empty();
        }
        return account.getTransactions().stream();
    }

    // Получение транзакций по счёту по указанным категориям. Получаем список транзакций и фильтруем их по типу
    public static Stream<Transaction> getTransactionsByAccountAndType(BankAccount account, TransactionType type) {
        return getTransactionsByAccount(account)
                .filter(transaction -> transaction.getType() == type);
    }

    // Получение транзакций по клиенту. Если клиента нет, то возвращается пустой список
    public static Stream<Transaction> getTransactionsByUser(User user) {
        if (user == null) {
            return Stream.empty();
        }
        return user.getBankAccounts().stream()
                .flatMap(account -> account.getTransactions().stream());
    }

    // Получение по клиенту по указанным категориям. Получаем список транзакций и фильтруем их по типу
    public static Stream<Transaction> getTransactionsByUserAndType(User user, TransactionType type) {
        return getTransactionsByUser(user)
                .filter(transaction -> transaction.getType() == type);
    }

    // Список транзакций, удовлетворяющих условию
    public static List<Transaction> filterTransactions(User user, Predicate<Transaction> predicate) {
        return getTransactionsByUser(user).filter(predicate).collect(Collectors.toList());
    }

    // Список строковых представлений транзакций
    public static List<String> transformTransactions(User user, Function<Transaction, String> function) {
        return getTransactionsByUser(user).map(function).collect(Collectors.toList());
    }

    // Применение функции к каждой транзакции
    public static void processTransactions(User user, Consumer<Transaction> consumer) {
        getTransactionsByUser(user).forEach(consumer);
    }

    // Возвращает созданный список транзакций
    public static List<Transaction> createTransactionList(Supplier<List<Transaction>> supplier) {
        if (supplier == null)
            return new ArrayList<>();

        return supplier.get();
    }

    // Возвращает объединённый список транзакций
    public static List<Transaction> mergeTransactionLists(
            List<Transaction> firstTransaction,
            List<Transaction> secondTransaction,
            BiFunction<List<Transaction>, List<Transaction>, List<Transaction>> biFunction
    ) {
        if (biFunction == null)
            return new ArrayList<>();

        return biFunction.apply(firstTransaction, secondTransaction);
    }
}
