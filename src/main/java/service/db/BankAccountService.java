package service.db;

import classifier.TransactionType;
import db.jdbc.DBManager;
import dto.CreateAccountRequest;
import entity.BankAccount;
import entity.Refund;
import entity.Transaction;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.notification.NotificationService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис отвечает за управление счетами, включая создание, удаление и пополнение
 */
@Service
public class BankAccountService {
    private final DBManager dbManager = DBManager.getInstance();

    private final PaymentGatewayService paymentGatewayService;
    private final RefundService refundService;
    private final NotificationService emailNotificationService;
    private final NotificationService smsNotificationService;

    @Value("${notification.recipient.email}")
    private String recipientEmail;
    @Value("${notification.recipient.phone}")
    private String recipientPhone;

    @Autowired
    public BankAccountService(PaymentGatewayService paymentGatewayService, RefundService refundService,
                              @Qualifier("emailNotificationService") NotificationService emailNotificationService,
                              @Qualifier("smsNotificationService") NotificationService smsNotificationService) {
        this.paymentGatewayService = paymentGatewayService;
        this.refundService = refundService;
        this.emailNotificationService = emailNotificationService;
        this.smsNotificationService = smsNotificationService;
    }

    public BankAccount findAccountByNumber(String number) {
        return dbManager.getBankAccount(number);
    }

    public BankAccount addBankAccount(CreateAccountRequest createAccountRequest) {
        BankAccount account = new BankAccount(
                "12345",
                createAccountRequest.getInitialBalance(),
                new User(
                        "999",
                        createAccountRequest.getOwnerName(),
                        createAccountRequest.getOwnerEmail(),
                        createAccountRequest.getOwnerPhone()
                )
        );

        dbManager.addBankAccount(account);
        return account;
    }

    public List<Transaction> getAccountTransactions(String number) {
        return dbManager.getTransactionsByBankAccount(findAccountByNumber(number));
    }

    public BankAccount withdraw(String number, BigDecimal amount) {
        BankAccount account = findAccountByNumber(number);
        User user = account.getUser();

        BigDecimal resultSum = account.getBalance().subtract(amount);
        if (resultSum.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("На счету недостаточно средств: " + account.getBalance());
            return null;
        }

        Transaction transaction = new Transaction(
                user,
                null,
                user,
                amount,
                TransactionType.PAYMENT,
                null
        );
        dbManager.addTransaction(transaction);
        dbManager.updateBalance(number, resultSum);
        boolean paymentGatewayServiceStatus = paymentGatewayService.authorize("Платеж по карте", amount);
        if (!paymentGatewayServiceStatus) {
            System.out.println("Ошибка подключения к сервису оплаты. Попробуйте позже");
            Refund refund = new Refund(
                    1,
                    amount,
                    "Description",
                    transaction
            );
            refundService.createRefund(refund);
            dbManager.updateBalance(number, account.getBalance());
        } else {
            smsNotificationService.sendNotification(recipientPhone, "Произошел платеж");
            emailNotificationService.sendNotification(recipientEmail, "Произошел платеж");
        }
        return account;
    }

    public BankAccount deposit(String number, BigDecimal amount) {
        BankAccount account = findAccountByNumber(number);
        User user = account.getUser();

        BigDecimal resultSum = account.getBalance().add(amount);

        Transaction transaction = new Transaction(
                null,
                null,
                user,
                amount,
                TransactionType.DEPOSIT,
                null
        );
        dbManager.addTransaction(transaction);
        dbManager.updateBalance(number, resultSum);
        boolean paymentGatewayServiceStatus = paymentGatewayService.authorize("Пополнение карты", amount);
        if (!paymentGatewayServiceStatus) {
            System.out.println("Ошибка подключения к сервису пополнения. Попробуйте позже");
            Refund refund = new Refund(
                    1,
                    amount,
                    "Description",
                    transaction
            );
            refundService.createRefund(refund);
            dbManager.updateBalance(number, account.getBalance());
        } else {
            smsNotificationService.sendNotification(recipientPhone, "Произошло пополнение средств");
            emailNotificationService.sendNotification(recipientEmail, "Произошло пополнение средств");
        }
        return account;
    }

    public void transfer(String fromAccountNumber, String toAccountNumber,
                         BigDecimal amount) {
        BankAccount fromAccount = findAccountByNumber(fromAccountNumber);
        BankAccount toAccount = findAccountByNumber(toAccountNumber);
        User fromUser = fromAccount.getUser();
        User toUser = toAccount.getUser();

        BigDecimal fromResultSum = fromAccount.getBalance().subtract(amount);
        BigDecimal toResultSum = toAccount.getBalance().add(amount);
        if (fromResultSum.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("На счету недостаточно средств для перевода: " + fromAccount.getBalance());
            return;
        }

        Transaction transactionPayment = new Transaction(
                fromUser,
                toUser,
                fromUser,
                amount,
                TransactionType.PAYMENT,
                null
        );
        dbManager.addTransaction(transactionPayment);
        dbManager.updateBalance(fromAccountNumber, fromResultSum);

        Transaction transactionDeposit = new Transaction(
                fromUser,
                toUser,
                toUser,
                amount,
                TransactionType.DEPOSIT,
                null
        );
        dbManager.addTransaction(transactionDeposit);
        dbManager.updateBalance(toAccountNumber, toResultSum);

        boolean paymentGatewayServiceStatus = paymentGatewayService.authorize("Перевод средств", amount);
        if (!paymentGatewayServiceStatus) {
            System.out.println("Ошибка подключения к сервису пополнения. Попробуйте позже");
            Refund refund = new Refund(
                    1,
                    amount,
                    "Description",
                    transactionPayment
            );
            refundService.createRefund(refund);
            dbManager.updateBalance(fromAccountNumber, fromAccount.getBalance());
            dbManager.updateBalance(toAccountNumber, toAccount.getBalance());
        } else {
            smsNotificationService.sendNotification(recipientPhone, "Произошло пополнение средств");
            emailNotificationService.sendNotification(recipientEmail, "Произошло пополнение средств");
        }
    }
}
