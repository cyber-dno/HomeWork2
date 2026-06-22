package Service;

import Entity.BankAccount;
import Entity.Transaction;
import Entity.User;
import Enum.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class TransactionService {
    // Получение транзакций по счёту. Если счёта нет, то возвращается пустой список
    public static List<Transaction> getTransactionsByAccount(BankAccount account) {
        if (account == null) {
            return new ArrayList<>();
        }

        return account.getTransactions();
    }

    // Получение транзакций по счёту по указанным категориям. Получаем список транзакций и фильтруем их по типу
    public static List<Transaction> getTransactionsByAccountAndType(BankAccount account, TransactionType type) {
        List<Transaction> allTransactions = getTransactionsByAccount(account);
        List<Transaction> filteredTransactions = allTransactions
                .stream()
                .filter(transaction -> transaction.getType() == type)
                .toList();

        return filteredTransactions;
    }

    // Получение транзакций по клиенту. Если клиента нет, то возвращается пустой список
    public static List<Transaction> getTransactionsByUser(User user) {
        List<Transaction> allTransactions = new ArrayList<>();
        if (user == null) {
            return allTransactions;
        }

        for (BankAccount account : user.getBankAccounts()) {
            allTransactions.addAll(account.getTransactions());
        }

        return allTransactions;
    }

    // Получение по клиенту по указанным категориям. Получаем список транзакций и фильтруем их по типу
    public static List<Transaction> getTransactionsByUserAndType(User user, TransactionType type) {
        List<Transaction> allTransactions = getTransactionsByUser(user);
        List<Transaction> filteredTransactions = allTransactions
                .stream()
                .filter(transaction -> transaction.getType() == type)
                .toList();

        return filteredTransactions;
    }
}
