package service;

import entity.BankAccount;
import entity.Transaction;
import entity.User;
import classifier.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AnalyticsService {
    // Получение месячных затрат по указанной категории
    public static BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category) {
        BigDecimal spendsByCategory = BigDecimal.ZERO;
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);

        // Фильтруем список транзакций по пользователю
        spendsByCategory = TransactionService.getTransactionsByAccountAndType(bankAccount, TransactionType.PAYMENT)
                .filter(transaction -> category.equals(transaction.getCategory()))
                .filter(transaction -> transaction.getCreatedDate().isAfter(border))
                // Получаем общие затраты по интересующим нас транзакциям
                .map(Transaction::getValue)
                .reduce(spendsByCategory, BigDecimal::add);

        return spendsByCategory;
    }

    // Получение месячных затрат по указанным категориям
    public static Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories) {
        Map<String, BigDecimal> spendingByCategories;
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);

        // Фильтруем список транзакций по пользователю
        spendingByCategories = TransactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT)
                .filter(transaction -> categories.contains(transaction.getCategory()))
                .filter(transaction -> transaction.getCreatedDate().isAfter(border))
                // Формируем словарь по затратам
                .collect(Collectors.toMap(
                        Transaction::getCategory,
                        Transaction::getValue,
                        BigDecimal::add
                ));

        return spendingByCategories;
    }

    // Получение истории транзакций по категориям. Транзакции в категории отсортированы по убыванию
    public static LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user) {
        LinkedHashMap<String, List<Transaction>> transactionHistory;

        // Фильтруем список транзакций по пользователю
        transactionHistory = TransactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT)
                // Сортируем транзакции по убыванию
                .sorted(Comparator.comparing(Transaction::getValue).reversed())
                // Формируем словарь по категориям
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return transactionHistory;
    }

    // Получение последних N транзакций по пользователю
    public static List<Transaction> getLastNTransactions(User user, int n) {
        List<Transaction> lastTransactions;

        // Получаем транзакции по пользователю и сортируем их по дате
        lastTransactions = TransactionService.getTransactionsByUser(user)
                .sorted(Comparator.comparing(Transaction::getCreatedDate).reversed())
                .limit(n)
                .toList();

        return lastTransactions;
    }

    // Получение наибольших N транзакций по пользователю
    public static PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n) {
        // Указываем компаратор, чтобы самой приоритетной была наименьшая транзакция
        PriorityQueue<Transaction> largestTransactions;

        largestTransactions = TransactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT)
                .sorted(Comparator.comparing(Transaction::getValue).reversed())
                .limit(n)
                .collect(Collectors.toCollection(
                        () -> new PriorityQueue<>(Comparator.comparing(Transaction::getValue).reversed())
                ));

        return largestTransactions;
    }
}
