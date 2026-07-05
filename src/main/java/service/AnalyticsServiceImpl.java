package service;

import annotation.LogExecutionTime;
import entity.BankAccount;
import entity.Transaction;
import entity.User;
import classifier.TransactionType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log
public class AnalyticsServiceImpl implements AnalyticsService {
    private final TransactionServiceImpl transactionService;

    public AnalyticsServiceImpl(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostConstruct
    public void initBean() {
        log.info("AnalyticsServiceImpl initialized");
    }

    @PreDestroy
    public void destroyBean() {
        log.info("AnalyticsServiceImpl destroyed");
    }

    // Получение месячных затрат по указанной категории
    @LogExecutionTime
    public BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category) {
        BigDecimal spendsByCategory = BigDecimal.ZERO;
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);

        // Фильтруем список транзакций по пользователю
        spendsByCategory = transactionService.getTransactionsByAccountAndType(bankAccount, TransactionType.PAYMENT)
                .filter(transaction -> category.equals(transaction.getCategory()))
                .filter(transaction -> transaction.getCreatedDate().isAfter(border))
                // Получаем общие затраты по интересующим нас транзакциям
                .map(Transaction::getValue)
                .reduce(spendsByCategory, BigDecimal::add);

        return spendsByCategory;
    }

    // Получение месячных затрат по указанным категориям
    public Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories) {
        Map<String, BigDecimal> spendingByCategories;
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);

        // Фильтруем список транзакций по пользователю
        spendingByCategories = transactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT)
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
    public LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user) {
        LinkedHashMap<String, List<Transaction>> transactionHistory;

        // Фильтруем список транзакций по пользователю
        transactionHistory = transactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT)
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
    public List<Transaction> getLastNTransactions(User user, int n) {
        List<Transaction> lastTransactions;

        // Получаем транзакции по пользователю и сортируем их по дате
        lastTransactions = transactionService.getTransactionsByUser(user)
                .sorted(Comparator.comparing(Transaction::getCreatedDate).reversed())
                .limit(n)
                .toList();

        return lastTransactions;
    }

    @LogExecutionTime
    // Получение наибольших N транзакций по пользователю
    public PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n) {
        // Указываем компаратор, чтобы самой приоритетной была наименьшая транзакция
        PriorityQueue<Transaction> largestTransactions;

        largestTransactions = transactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT)
                .sorted(Comparator.comparing(Transaction::getValue).reversed())
                .limit(n)
                .collect(Collectors.toCollection(
                        () -> new PriorityQueue<>(Comparator.comparing(Transaction::getValue).reversed())
                ));

        return largestTransactions;
    }
}
