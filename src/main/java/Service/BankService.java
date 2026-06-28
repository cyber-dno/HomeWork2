package Service;

import Entity.BankAccount;
import Entity.Transaction;
import Entity.User;

import java.math.BigDecimal;
import java.util.*;

public interface BankService {
    BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category);

    Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories);

    LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user);

    List<Transaction> getLastNTransactions(User user, int n);

    PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n);
}
