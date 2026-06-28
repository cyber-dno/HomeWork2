import Annotation.LogExecutionTimeProcessor;
import Entity.BankAccount;
import Entity.Transaction;
import Entity.User;
import Enum.TransactionType;
import Service.BankService;
import Service.TransactionService;
import Service.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class TransactionServiceTest {
    TransactionServiceImpl transactionService = TransactionServiceImpl.getINSTANCE();
    TransactionService proxyService = (TransactionService) Proxy.newProxyInstance(
            transactionService.getClass().getClassLoader(),
            transactionService.getClass().getInterfaces(),
            new LogExecutionTimeProcessor(transactionService)
    );

    private static User user = new User();
    private static User user2 = new User();

    private static BankAccount bankAccount1;
    private static BankAccount bankAccount2;
    private static BankAccount bankAccount3;
    private static BankAccount bankAccount4;

    private static final BigDecimal ONE_DOLLAR = new BigDecimal("1.0");
    private static final BigDecimal TWO_DOLLARS = new BigDecimal("2.0");
    private static final BigDecimal FIVE_DOLLARS = new BigDecimal("5.0");
    private static final BigDecimal TEN_DOLLARS = new BigDecimal("10.0");

    private static final String BEAUTY_CATEGORY = "Beauty";
    private static final String HEALTH_CATEGORY = "Health";
    private static final String EDUCATION_CATEGORY = "Education";

    private static final LocalDateTime ONE_MONTH_AGO = LocalDateTime.now().minusMonths(1L);
    private static final LocalDateTime TWO_MONTH_AGO = LocalDateTime.now().minusMonths(2L);
    private static final LocalDateTime ONE_WEEK_AGO = LocalDateTime.now().minusWeeks(1L);
    private static final LocalDateTime FIVE_WEEK_AGO = LocalDateTime.now().minusWeeks(5L);

    @BeforeAll
    public static void initializeData() {
        List<Transaction> testTransactionOnlyDepositType = new ArrayList<>();
        testTransactionOnlyDepositType.add(new Transaction("1", TWO_DOLLARS
                , TransactionType.DEPOSIT, BEAUTY_CATEGORY, ONE_WEEK_AGO));
        testTransactionOnlyDepositType.add(new Transaction("2", TEN_DOLLARS
                , TransactionType.DEPOSIT, HEALTH_CATEGORY, ONE_WEEK_AGO));
        testTransactionOnlyDepositType.add(new Transaction("3", ONE_DOLLAR
                , TransactionType.DEPOSIT, EDUCATION_CATEGORY, ONE_WEEK_AGO));

        List<Transaction> testTransactionDifTime = new ArrayList<>();
        testTransactionDifTime.add(new Transaction("1", TWO_DOLLARS
                , TransactionType.PAYMENT, BEAUTY_CATEGORY, ONE_MONTH_AGO));
        testTransactionDifTime.add(new Transaction("2", TEN_DOLLARS
                , TransactionType.PAYMENT, HEALTH_CATEGORY, FIVE_WEEK_AGO));
        testTransactionDifTime.add(new Transaction("3", ONE_DOLLAR
                , TransactionType.PAYMENT, EDUCATION_CATEGORY, TWO_MONTH_AGO));

        List<Transaction> TypeAndTime = Stream.concat(testTransactionOnlyDepositType.stream()
                , testTransactionDifTime.stream()).toList();

        List<Transaction> OneTransaction = new ArrayList<>();
        OneTransaction.add(new Transaction("1", TWO_DOLLARS
                , TransactionType.PAYMENT, BEAUTY_CATEGORY, ONE_MONTH_AGO));

        bankAccount1 = new BankAccount("001", TypeAndTime);
        bankAccount2 = new BankAccount("002", testTransactionOnlyDepositType);
        bankAccount3 = new BankAccount("003", testTransactionDifTime);
        bankAccount4 = new BankAccount("004", OneTransaction);

        user.getBankAccounts().add(bankAccount2);
        user.getBankAccounts().add(bankAccount3);
        user2.getBankAccounts().add(bankAccount4);
    }

    /*
        Метод для получения списка транзакций по счёту
     */

    // Проверяем работоспособность метода
    @Test
    public void getTransactionsByAccount() {
        List<Transaction> result = proxyService.getTransactionsByAccount(bankAccount1).toList();
        Assertions.assertEquals(result, bankAccount1.getTransactions());
    }

    // Подаём на вход пустой счёт
    @Test
    public void getEmptyTransactionsByAccount() {
        List<Transaction> result = proxyService.getTransactionsByAccount(null).toList();
        Assertions.assertTrue(result.isEmpty());
    }

    /*
        Метод для получения списка транзакций по счёту и указанному типу транзакции
     */

    // Проверяем работоспособность метода
    @Test
    public void getTransactionsByAccountAndType() {
        List<Transaction> result = proxyService.getTransactionsByAccountAndType(bankAccount1, TransactionType.PAYMENT).toList();
        Assertions.assertEquals(result, bankAccount3.getTransactions());
    }

    // Подаём на вход пустой счёт
    @Test
    public void getEmptyTransactionsByAccountAndType() {
        List<Transaction> result = proxyService.getTransactionsByAccountAndType(null, TransactionType.PAYMENT).toList();
        Assertions.assertTrue(result.isEmpty());
    }

    /*
        Метод для получения списка транзакций по пользователю
     */

    // Проверяем работоспособность метода
    @Test
    public void getTransactionsByUser() {
        List<Transaction> result = proxyService.getTransactionsByUser(user).toList();
        Assertions.assertEquals(result, bankAccount1.getTransactions());
    }

    // Подаём на вход пустого пользователя
    @Test
    public void getEmptyTransactionsByUser() {
        List<Transaction> result = proxyService.getTransactionsByUser(null).toList();
        Assertions.assertTrue(result.isEmpty());
    }

    /*
        Метод для получения списка транзакций по пользователю и указанному типу транзакции
     */

    // Проверяем работоспособность метода
    @Test
    public void getTransactionsByUserAndType() {
        List<Transaction> result = proxyService.getTransactionsByUserAndType(user, TransactionType.PAYMENT).toList();
        Assertions.assertEquals(result, bankAccount3.getTransactions());
    }

    // Подаём на вход пустого пользователя
    @Test
    public void getEmptyTransactionsByUserAndType() {
        List<Transaction> result = proxyService.getTransactionsByUserAndType(null, TransactionType.PAYMENT).toList();
        Assertions.assertTrue(result.isEmpty());
    }

    // Замеряем время выполнения обычных и параллельных стримов
    @Test
    public void analyzePerformance() {
        long startTime = System.currentTimeMillis();
        proxyService.getTransactionsByUser(user)
                .filter(transaction -> transaction.getValue().compareTo(TWO_DOLLARS) > 0)
                .sorted(Comparator.comparing(Transaction::getValue).reversed())
                .count();
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential time: " + (endTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        proxyService.getTransactionsByUser(user)
                .parallel()
                .filter(transaction -> transaction.getValue().compareTo(TWO_DOLLARS) > 0)
                .sorted(Comparator.comparing(Transaction::getValue).reversed())
                .count();
        endTime = System.currentTimeMillis();
        System.out.println("Parallel time: " + (endTime - startTime) + "ms");
    }

    @Test
    public void getFilterTransactions() {
        List<Transaction> result = transactionService.filterTransactions(
                user,
                transaction -> BEAUTY_CATEGORY.equals(transaction.getCategory())
                && TransactionType.DEPOSIT.equals(transaction.getType())
        );
        Assertions.assertEquals(TWO_DOLLARS, result.get(0).getValue());
    }

    @Test
    public void getTransformTransactions() {
        List<String> result = transactionService.transformTransactions(
                user2,
                Transaction::toString
        );
        Assertions.assertEquals(String.class, result.get(0).getClass());
    }

    @Test
    public void getProcessTransactions() {
        transactionService.processTransactions(
                user2,
                transaction -> transaction.setValue(BigDecimal.ZERO)
        );
        Assertions.assertEquals(BigDecimal.ZERO, user2.getBankAccounts().get(0).getTransactions().get(0).getValue());
    }

    @Test
    public void getCreateTransactionList() {
        List<Transaction> result = transactionService.createTransactionList(ArrayList::new);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getMergeTransactionLists() {
        List<Transaction> result = transactionService.mergeTransactionLists(
                bankAccount2.getTransactions(),
                bankAccount3.getTransactions(),
                (first, second) -> {
                    List<Transaction> list = new ArrayList<>(first);
                    list.addAll(second);
                    return list;
                }
        );
        Assertions.assertEquals(bankAccount1.getTransactions(), result);
    }
}
