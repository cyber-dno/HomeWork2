package service.db;

import entity.BankAccount;
import entity.Transaction;
import entity.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

public interface AnalyticsService {
    BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category, Connection connection);

    Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories, Connection connection);

    LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user, Connection connection);

    List<Transaction> getLastNTransactions(User user, int n, Connection connection);

    PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n, Connection connection);
}
