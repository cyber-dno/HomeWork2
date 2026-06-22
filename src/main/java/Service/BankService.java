package Service;

import Entity.BankAccount;
import Entity.Transaction;
import Entity.User;
import Enum.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class BankService {
    // Получение месячных затрат по указанной категории
    public static BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category) {
        BigDecimal spendsByCategory = BigDecimal.ZERO;
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);

        // Фильтруем список транзакций по пользователю
        List<Transaction> transactions = TransactionService.getTransactionsByAccountAndType(bankAccount, TransactionType.PAYMENT);
        transactions = transactions.stream()
                .filter(transaction -> category.equals(transaction.getCategory()))
                .filter(transaction -> transaction.getCreatedDate().isAfter(border))
                .toList();

        // Получаем общие затраты по интересующим нас транзакциям
        for (Transaction transaction : transactions) {
            BigDecimal value = transaction.getValue();
            spendsByCategory = spendsByCategory.add(value);
        }

        return spendsByCategory;
    }

    // Получение месячных затрат по указанным категориям
    public static Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories) {
        LocalDateTime border = LocalDateTime.now().minusMonths(1L);
        Map<String, BigDecimal> spendingByCategories = new HashMap<>();

        // Фильтруем список транзакций по пользователю
        List<Transaction> transactions = TransactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT);
        transactions = transactions.stream()
                .filter(transaction -> categories.contains(transaction.getCategory()))
                .filter(transaction -> transaction.getCreatedDate().isAfter(border))
                .toList();

        // Формируем словарь по затратам
        for (Transaction transaction : transactions) {
            if ((spendingByCategories.containsKey(transaction.getCategory()))) {
                // Если такая категория уже есть, то прибавляем затраты по транзакции к текущей
                BigDecimal value = spendingByCategories.get(transaction.getCategory());
                value = value.add(transaction.getValue());
                spendingByCategories.put(transaction.getCategory(), value);
            } else {
                // Если такой категории нет, то инициализируем новую
                spendingByCategories.put(transaction.getCategory(), transaction.getValue());
            }
        }

        return spendingByCategories;
    }

    // Получение истории транзакций по категориям. Транзакции в категории отсортированы по убыванию
    public static LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user) {
        LinkedHashMap<String, List<Transaction>> transactionHistory = new LinkedHashMap<>();

        // Формируем словарь по категориям
        List<Transaction> transactions = TransactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT);
        for (Transaction transaction : transactions) {
            if ((transactionHistory.containsKey(transaction.getCategory()))) {
                // Если категория уже существует, то добавляем транзакцию туда
                transactionHistory.get(transaction.getCategory()).add(transaction);
            } else {
                // Если категории ещё нет, то инициализируем новую
                transactionHistory.put(transaction.getCategory(), new ArrayList<>(List.of(transaction)));
            }
        }

        // Сортируем транзакции по убыванию
        for (List<Transaction> categoryTransactions : transactionHistory.values()) {
            categoryTransactions.sort(Comparator.comparing(Transaction::getValue).reversed());
        }

        return transactionHistory;
    }

    // Получение последних N транзакций по пользователю
    public static List<Transaction> getLastNTransactions(User user, int n) {
        List<Transaction> lastTransactions = new ArrayList<>();

        // Получаем транзакции по пользователю и сортируем их
        List<Transaction> transactions = TransactionService.getTransactionsByUser(user);
        transactions.sort(Comparator.comparing(Transaction::getCreatedDate).reversed());

        // Формируем список транзакций
        for (int i = 0; i < Math.min(n, transactions.size()); i++) {
            lastTransactions.add(transactions.get(i));
        }

        return lastTransactions;
    }

    // Получение наибольших N транзакций по пользователю
    public static PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n) {
        // Указываем компаратор, чтобы самой приоритетной была наименьшая транзакция
        PriorityQueue<Transaction> largestTransactions = new PriorityQueue<>(Comparator.comparing(Transaction::getValue));

        // Формируем список наибольших транзакций
        List<Transaction> transactions = TransactionService.getTransactionsByUserAndType(user, TransactionType.PAYMENT);
        for (Transaction transaction : transactions) {
            if (largestTransactions.size() < n) {
                // Если очередь ещё не забита, то просто добавляем транзакцию
                largestTransactions.offer(transaction);
            } else if (largestTransactions.peek()!= null
                    && largestTransactions.peek().getValue().compareTo(transaction.getValue()) < 0) {
                // Если очередь забита и пришла транзакция такая же или больше самой приоритетной (минимальной) в очереди,
                //  то извлекаем приоритетную транзакцию и вставляем в очередь новую
                largestTransactions.poll();
                largestTransactions.offer(transaction);
            }
        }

        return largestTransactions;
    }
}
