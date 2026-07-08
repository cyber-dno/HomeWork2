package service.db;

import annotation.LogExecutionTime;
import classifier.TransactionType;
import entity.BankAccount;
import entity.Transaction;
import entity.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log
public class AnalyticServiceBDImpl  implements AnalyticsService {
    private final TransactionServiceBDImpl transactionService;

    public AnalyticServiceBDImpl(TransactionServiceBDImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostConstruct
    public void initBean() {
        log.info("AnalyticServiceBDImpl initialized");
    }

    @PreDestroy
    public void destroyBean() {
        log.info("AnalyticServiceBDImpl destroyed");
    }

    // Получение месячных затрат по указанной категории
    @LogExecutionTime
    public BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category, Connection connection) {
        BigDecimal spendsByCategory = BigDecimal.ZERO;
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);

        // Фильтруем список транзакций по пользователю
        spendsByCategory = transactionService.getTransactionsByAccountAndType(bankAccount, TransactionType.PAYMENT, connection)
                .filter(transaction -> category.equals(transaction.getCategory()))
                .filter(transaction -> transaction.getDate().isAfter(border))
                // Получаем общие затраты по интересующим нас транзакциям
                .map(Transaction::getAmount)
                .reduce(spendsByCategory, BigDecimal::add);

        return spendsByCategory;
    }

    // Получение месячных затрат по указанным категориям
    public Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories, Connection connection) {
        Map<String, BigDecimal> spendingByCategories;
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);

        // Фильтруем список транзакций по пользователю
        spendingByCategories = transactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT, connection)
                .filter(transaction -> categories.contains(transaction.getCategory()))
                .filter(transaction -> transaction.getDate().isAfter(border))
                // Формируем словарь по затратам
                .collect(Collectors.toMap(
                        Transaction::getCategory,
                        Transaction::getAmount,
                        BigDecimal::add
                ));

        return spendingByCategories;
    }

    // Получение истории транзакций по категориям. Транзакции в категории отсортированы по убыванию
    public LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user, Connection connection) {
        LinkedHashMap<String, List<Transaction>> transactionHistory;

        // Фильтруем список транзакций по пользователю
        transactionHistory = transactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT, connection)
                // Сортируем транзакции по убыванию
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                // Формируем словарь по категориям
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return transactionHistory;
    }

    // Получение последних N транзакций по пользователю
    public List<Transaction> getLastNTransactions(User user, int n, Connection connection) {
        List<Transaction> lastTransactions;

        // Получаем транзакции по пользователю и сортируем их по дате
        lastTransactions = transactionService.getTransactionsByUser(user, connection)
                .sorted(Comparator.comparing(Transaction::getDate).reversed())
                .limit(n)
                .toList();

        return lastTransactions;
    }

    @LogExecutionTime
    // Получение наибольших N транзакций по пользователю
    public PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n, Connection connection) {
        // Указываем компаратор, чтобы самой приоритетной была наименьшая транзакция
        PriorityQueue<Transaction> largestTransactions;

        largestTransactions = transactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT, connection)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .limit(n)
                .collect(Collectors.toCollection(
                        () -> new PriorityQueue<>(Comparator.comparing(Transaction::getAmount).reversed())
                ));

        return largestTransactions;
    }
}
