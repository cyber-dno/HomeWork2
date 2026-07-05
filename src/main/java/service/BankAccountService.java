package service;

import classifier.TransactionType;
import entity.BankAccount;
import entity.Transaction;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.notification.NotificationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис отвечает за управление счетами, включая создание, удаление и пополнение
 */
@Service
public class BankAccountService {
    private final PaymentGatewayService paymentGatewayService;
    private final NotificationService emailNotificationService;
    private final NotificationService smsNotificationService;

    @Value("${notification.recipient.email}")
    private String recipientEmail;
    @Value("${notification.recipient.phone}")
    private String recipientPhone;

    @Autowired
    public BankAccountService(PaymentGatewayService paymentGatewayService,
                              @Qualifier("emailNotificationService") NotificationService emailNotificationService,
                              @Qualifier("smsNotificationService") NotificationService smsNotificationService) {
        this.paymentGatewayService = paymentGatewayService;
        this.emailNotificationService = emailNotificationService;
        this.smsNotificationService = smsNotificationService;
    }

    public BankAccount findAccountById(int accountId) {
        return createTestAccount();
    }

    public void withdraw(BankAccount account, BigDecimal amount) {
        account.setBalance(account.getBalance().subtract(amount));
    }

    public BankAccount withdraw(Integer accountId, BigDecimal amount) {
        BankAccount account = findAccountById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        return account;
    }

    public BankAccount deposit(Integer accountId, BigDecimal amount) {
        BankAccount account = findAccountById(accountId);
        account.setBalance(account.getBalance().add(amount));
        return account;
    }

    public void transfer(Integer fromAccountId, Integer toAccountId,
                         BigDecimal amount) {
        withdraw(fromAccountId, amount);
        deposit(toAccountId, amount);
    }

    public void processCardPayment(BankAccount account, BigDecimal amount) {
        withdraw(account, amount);
        //создание транзакции
        System.out.println("Processed card payment for account " + account.getId());
        paymentGatewayService.authorize("Платеж по карте", amount);
        smsNotificationService.sendNotification(recipientPhone, "Произошел платеж по карте");
        emailNotificationService.sendNotification(recipientEmail, "Произошел платеж по карте");
    }

    public void processBankTransfer(BankAccount account, BigDecimal amount) {
        withdraw(account, amount);
        //создание транзакции
        System.out.println("Processed bank transfer for account " + account.getId());
        paymentGatewayService.authorize("Платеж по карте", amount);
        smsNotificationService.sendNotification(recipientPhone, "Произошел платеж по карте");
        emailNotificationService.sendNotification(recipientEmail, "Произошел платеж по карте");
    }

    public void processWalletPayment(BankAccount account, BigDecimal amount) {
        withdraw(account, amount);
        //создание транзакции
        System.out.println("Processed wallet payment for account " + account.getId());
        paymentGatewayService.authorize("Платеж по карте", amount);
        smsNotificationService.sendNotification(recipientPhone, "Произошел платеж по карте");
        emailNotificationService.sendNotification(recipientEmail, "Произошел платеж по карте");
    }

    public BankAccount createTestAccount() {
        User testUser = new User();
        testUser.setId("user123");
        testUser.setFirstName("John");
        testUser.setMiddleName("K");
        testUser.setLastName("Doe");
        testUser.setBirthDate(LocalDateTime.now().minusYears(25).toLocalDate());
        testUser.setEmail("john.doe@example.com");
        testUser.setPhoneNumber("+1234567890");

        BankAccount account = new BankAccount();
        account.setId("acc123");
        account.setBalance(new BigDecimal("5000.00"));
        account.setOwner(testUser);

        Transaction transaction1 = new Transaction(
                "tx001",
                new BigDecimal("100.00"),
                TransactionType.PAYMENT,
                "Electronics",
                LocalDateTime.now().minusDays(5)
        );

        Transaction transaction2 = new Transaction(
                "tx002",
                new BigDecimal("200.00"),
                TransactionType.DEPOSIT,
                "Groceries",
                LocalDateTime.now().minusDays(2)
        );

        account.getTransactions().addAll(List.of(transaction1, transaction2));

        return account;
    }
}
