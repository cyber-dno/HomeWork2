import Entity.BankAccount;
import Entity.Transaction;
import Entity.User;
import Service.BankService;
import Enum.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class BankServiceTest {
    private static final User user = new User();
    private static final User user2 = new User();
    private static final User user3 = new User();
    private static final User user4 = new User();

    private static BankAccount bankAccount1;
    private static BankAccount bankAccount2;
    private static BankAccount bankAccount3;

    private static final BigDecimal ONE_DOLLAR = new BigDecimal("1.0");
    private static final BigDecimal TWO_DOLLARS = new BigDecimal("2.0");
    private static final BigDecimal FIVE_DOLLARS = new BigDecimal("5.0");
    private static final BigDecimal SEVEN_DOLLARS = new BigDecimal("7.0");
    private static final BigDecimal TEN_DOLLARS = new BigDecimal("10.0");

    private static final String BEAUTY_CATEGORY = "Beauty";
    private static final String HEALTH_CATEGORY = "Health";
    private static final String EDUCATION_CATEGORY = "Education";

    private static final LocalDateTime ONE_MONTH_AGO = LocalDateTime.now().minusMonths(1L);
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
                , TransactionType.DEPOSIT, BEAUTY_CATEGORY, ONE_WEEK_AGO));

        List<Transaction> testTransactionDifTime = new ArrayList<>();
        testTransactionDifTime.add(new Transaction("1", TWO_DOLLARS
                , TransactionType.PAYMENT, EDUCATION_CATEGORY, ONE_WEEK_AGO));
        testTransactionDifTime.add(new Transaction("2", TEN_DOLLARS
                , TransactionType.PAYMENT, EDUCATION_CATEGORY, FIVE_WEEK_AGO));
        testTransactionDifTime.add(new Transaction("3", ONE_DOLLAR
                , TransactionType.PAYMENT, EDUCATION_CATEGORY, ONE_MONTH_AGO));
        testTransactionDifTime.add(new Transaction("4", FIVE_DOLLARS
                , TransactionType.PAYMENT, EDUCATION_CATEGORY, ONE_WEEK_AGO));

        List<Transaction> TypeAndTime = Stream.concat(testTransactionOnlyDepositType.stream()
                , testTransactionDifTime.stream()).toList();

        bankAccount1 = new BankAccount("001", TypeAndTime);
        bankAccount2 = new BankAccount("002", testTransactionOnlyDepositType);
        bankAccount3 = new BankAccount("003", testTransactionDifTime);

        user.getBankAccounts().add(bankAccount2);
        user.getBankAccounts().add(bankAccount3);
        user2.getBankAccounts().add(bankAccount2);
        user3.getBankAccounts().add(bankAccount3);
    }

    /*
        Вывод суммы потраченных средств на категорию X за последний месяц со счета Y
        Метод возвращает сумму потраченных средств на указанную категорию за последний месяц
        - Учитываются только транзакции типа `PAYMENT`.
        - Если категория не существует или счет равен `null`, метод возвращает `BigDecimal.ZERO`.
        - Транзакции старше одного месяца от текущей даты не учитываются.
     */

    // Подаём на вход счёт только с депозитами
    @Test
    public void getMonthlySpendingByCategoryOnlyDeposit() {
        BigDecimal result = BankService.getMonthlySpendingByCategory(bankAccount2, EDUCATION_CATEGORY);
        Assertions.assertEquals(BigDecimal.ZERO, result);
    }

    // Подаём на вход пустой счёт
    @Test
    public void getMonthlySpendingByCategoryByEmptyAccount() {
        BigDecimal result = BankService.getMonthlySpendingByCategory(null, EDUCATION_CATEGORY);
        Assertions.assertEquals(BigDecimal.ZERO, result);
    }

    // Подаём на вход несуществующую категорию
    @Test
    public void getMonthlySpendingByWrongCategory() {
        BigDecimal result = BankService.getMonthlySpendingByCategory(bankAccount1, "TestCategory");
        Assertions.assertEquals(BigDecimal.ZERO, result);
    }

    // Проверяем работоспособность метода
    @Test
    public void getMonthlySpendingByCategory() {
        BigDecimal result = BankService.getMonthlySpendingByCategory(bankAccount1, EDUCATION_CATEGORY);
        Assertions.assertEquals(SEVEN_DOLLARS, result);
    }

    /*
        Вывод информации о том, сколько было потрачено средств на N категорий за последний месяц со всех счетов
        Метод возвращает Map, где ключом является категория, а значением — сумма потраченных средств за последний месяц.
        - Учитываются только транзакции типа `PAYMENT`.
        - Если пользователь равен `null` или категории не существуют, метод возвращает пустую `Map`.
     */

    // Подаём на вход категории только с депозитами
    @Test
    public void getMonthlySpendingByCategoriesOnlyDeposit() {
        Map<String, BigDecimal> result = BankService.getMonthlySpendingByCategories(user, Set.of(BEAUTY_CATEGORY, HEALTH_CATEGORY));
        Assertions.assertTrue(result.isEmpty());
    }

    // Подаём на вход пустого пользователя
    @Test
    public void getMonthlySpendingByCategoriesByEmptyUser() {
        Map<String, BigDecimal> result = BankService.getMonthlySpendingByCategories(null, Set.of(BEAUTY_CATEGORY, HEALTH_CATEGORY));
        Assertions.assertTrue(result.isEmpty());
    }

    // Подаём на вход несуществующую категорию
    @Test
    public void getMonthlySpendingByWrongCategories() {
        Map<String, BigDecimal> result = BankService.getMonthlySpendingByCategories(user, Set.of("TestCategory"));
        Assertions.assertTrue(result.isEmpty());
    }

    // Проверяем работоспособность метода
    @Test
    public void getMonthlySpendingByCategories() {
        Map<String, BigDecimal> result = BankService.getMonthlySpendingByCategories(user, Set.of(HEALTH_CATEGORY, EDUCATION_CATEGORY));
        Assertions.assertTrue(result.containsKey(EDUCATION_CATEGORY));
        Assertions.assertFalse(result.containsKey(HEALTH_CATEGORY));
        Assertions.assertEquals(SEVEN_DOLLARS, result.get(EDUCATION_CATEGORY));
    }

    /*
        Вывод истории операций по всем счетам и по всем категориям от наибольшей к наименьшей
        Метод возвращает LinkedHashMap, где ключом является категория, а значением — список транзакций, отсортированных
        по сумме от наибольшей к наименьшей.
        - Учитываются только транзакции типа `PAYMENT`.
        - Если пользователь равен `null`, метод возвращает пустую `LinkedHashMap`.
     */

    // Подаём на вход пользователя только с депозитами
    @Test
    public void getTransactionHistoryOnlyDeposit() {
        LinkedHashMap<String, List<Transaction>> result = BankService.getTransactionHistorySortedByAmount(user2);
        Assertions.assertTrue(result.isEmpty());
    }

    // Подаём на вход пустого пользователя
    @Test
    public void getTransactionHistoryByEmptyUser() {
        LinkedHashMap<String, List<Transaction>> result = BankService.getTransactionHistorySortedByAmount(null);
        Assertions.assertTrue(result.isEmpty());
    }

    // Проверяем работоспособность метода
    @Test
    public void getTransactionHistory() {
        LinkedHashMap<String, List<Transaction>> result = BankService.getTransactionHistorySortedByAmount(user);
        Assertions.assertTrue(result.containsKey(EDUCATION_CATEGORY));
        Assertions.assertFalse(result.containsKey(HEALTH_CATEGORY));
        Assertions.assertEquals(TEN_DOLLARS, result.get(EDUCATION_CATEGORY).get(0).getValue());
        Assertions.assertEquals(ONE_DOLLAR, result.get(EDUCATION_CATEGORY).get(3).getValue());
    }

    /*
        Вывод последних N транзакций пользователя
        Метод возвращает последние N транзакций пользователя.
        - сли транзакций меньше `N`, возвращается их фактическое количество.
        - Если транзакций нет или пользователь равен `null`, метод возвращает пустой список.
     */

    // Подаём на вход пользователя без транзакций
    @Test
    public void getLastTransactionsByUserWithoutTransactions() {
        List<Transaction> result = BankService.getLastNTransactions(user4, 5);
        Assertions.assertTrue(result.isEmpty());
    }

    // Подаём на вход пустого пользователя
    @Test
    public void getLastTransactionsByEmptyUser() {
        List<Transaction> result = BankService.getLastNTransactions(null, 5);
        Assertions.assertTrue(result.isEmpty());
    }

    // Проверяем работоспособность метода
    @Test
    public void getLastTransactions() {
        List<Transaction> result = BankService.getLastNTransactions(user3, 3);
        Assertions.assertEquals(ONE_WEEK_AGO, result.get(0).getCreatedDate());
        Assertions.assertEquals(ONE_MONTH_AGO, result.get(2).getCreatedDate());
    }

    /*
        Вывод топ-N самых больших платежных транзакций пользователя
        Метод возвращает PriorityQueue, содержащую топ-N самых больших транзакций.
        - Учитываются только транзакции типа `PAYMENT`.
        - Если транзакций меньше `N`, возвращаются все доступные.
        - Если транзакций нет или пользователь равен `null`, метод возвращает пустую очередь.
     */

    // Подаём на вход пользователя без транзакций
    @Test
    public void getLargestTransactionsByUserWithoutTransactions() {
        PriorityQueue<Transaction> result = BankService.getTopNLargestTransactions(user4, 5);
        Assertions.assertTrue(result.isEmpty());
    }

    // Подаём на вход пользователя только с депозитами
    @Test
    public void getLargestTransactionsByUserWithoutPayment() {
        PriorityQueue<Transaction> result = BankService.getTopNLargestTransactions(user2, 5);
        Assertions.assertTrue(result.isEmpty());
    }

    // Подаём на вход пустого пользователя
    @Test
    public void getLargestTransactionsByEmptyUser() {
        PriorityQueue<Transaction> result = BankService.getTopNLargestTransactions(null, 5);
        Assertions.assertTrue(result.isEmpty());
    }

    // Проверяем работоспособность метода
    @Test
    public void getLargestTransactions() {
        PriorityQueue<Transaction> result = BankService.getTopNLargestTransactions(user, 2);
        Assertions.assertEquals(FIVE_DOLLARS, result.poll().getValue());
    }
}
