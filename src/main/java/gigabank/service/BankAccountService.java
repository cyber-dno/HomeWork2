package gigabank.service;

import gigabank.classifier.Currency;
import gigabank.dto.CreateAccountRequest;
import gigabank.exception.AccountNoFundsException;
import gigabank.exception.AccountNotFoundException;
import gigabank.model.BankAccount;
import gigabank.model.Refund;
import gigabank.model.Transaction;
import gigabank.model.User;
import gigabank.repository.BankAccountRepository;
import gigabank.repository.TransactionRepository;
import gigabank.repository.UserRepository;
import gigabank.service.notification.NotificationService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Сервис отвечает за управление счетами, включая создание, удаление и пополнение
 */
@Service
public class BankAccountService {
    private final PaymentGatewayService paymentGatewayService;
    private final RefundService refundService;
    private final NotificationService emailNotificationService;
    private final NotificationService smsNotificationService;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public BankAccountService(PaymentGatewayService paymentGatewayService,
                              RefundService refundService,
                              @Qualifier("emailNotificationService") NotificationService emailNotificationService,
                              @Qualifier("smsNotificationService") NotificationService smsNotificationService,
                              BankAccountRepository bankAccountRepository,
                              TransactionRepository transactionRepository,
                              UserRepository userRepository) {
        this.paymentGatewayService = paymentGatewayService;
        this.refundService = refundService;
        this.emailNotificationService = emailNotificationService;
        this.smsNotificationService = smsNotificationService;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Page<BankAccount> getAccounts(int page, int size) {
        return bankAccountRepository.findAll(PageRequest.of(page, size));
    }

    public boolean closeAccount(String id) {
        BankAccount account = bankAccountRepository.getBankAccountById(Long.valueOf(id));
        if (account == null) {
            throw new AccountNotFoundException(Long.valueOf(id));
        }

        bankAccountRepository.delete(account);
        return true;
    }

    public BankAccount findAccountByNumber(String number) {
        BankAccount account = bankAccountRepository.getBankAccountByAccountNumber(number);
        if (account == null) {
            throw new AccountNotFoundException(number);
        }
        return account;
    }

    public BankAccount addBankAccount(CreateAccountRequest createAccountRequest) {
        BankAccount account = new BankAccount();
        account.setBalance(createAccountRequest.getInitialBalance());
        account.setAccountNumber(String.valueOf(UUID.randomUUID()));
        account.setCurrency(Currency.RUB);

        List<User> user = userRepository.getUserByEmail_AndName(
                createAccountRequest.getOwnerEmail(),
                createAccountRequest.getOwnerName()
        );
        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setName(createAccountRequest.getOwnerName());
            newUser.setEmail(createAccountRequest.getOwnerEmail());
            userRepository.save(newUser);

            user.add(newUser);
        }
        account.setUser(user.get(0));

        bankAccountRepository.save(account);
        return account;
    }

    public List<Transaction> getAccountTransactions(String number) {
        return transactionRepository.getTransactionsByAccount_AccountNumber(number);
    }

    @Transactional
    public BankAccount withdraw(String number, BigDecimal amount) {
        BankAccount account = findAccountByNumber(number);

        BigDecimal resultSum = account.getBalance().subtract(amount);
        if (resultSum.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountNoFundsException(number);
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setDescription("Платеж по карте");
        transactionRepository.save(transaction);

        account.setBalance(resultSum);
        bankAccountRepository.save(account);

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
            account.setBalance(account.getBalance());
            bankAccountRepository.save(account);
        } else {
            emailNotificationService.sendNotification(account.getUser().getEmail(), "Произошел платеж");
        }
        return account;
    }

    @Transactional
    public BankAccount deposit(String number, BigDecimal amount) {
        BankAccount account = findAccountByNumber(number);

        BigDecimal resultSum = account.getBalance().add(amount);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setDescription("Пополнение карты");
        transactionRepository.save(transaction);

        account.setBalance(resultSum);
        bankAccountRepository.save(account);

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
            account.setBalance(account.getBalance());
            bankAccountRepository.save(account);
        } else {
            emailNotificationService.sendNotification(account.getUser().getEmail(), "Произошло пополнение средств");
        }
        return account;
    }

    @Transactional
    public void transfer(String fromAccountNumber, String toAccountNumber,
                         BigDecimal amount) {
        BankAccount fromAccount = findAccountByNumber(fromAccountNumber);
        BankAccount toAccount = findAccountByNumber(toAccountNumber);

        if (fromAccount == null) {
            throw new AccountNotFoundException(fromAccountNumber);
        } else if (toAccount == null) {
            throw new AccountNotFoundException(toAccountNumber);
        }

        BigDecimal fromResultSum = fromAccount.getBalance().subtract(amount);
        BigDecimal toResultSum = toAccount.getBalance().add(amount);
        if (fromResultSum.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountNoFundsException(fromAccountNumber);
        }

        Transaction transactionPayment = new Transaction();
        transactionPayment.setAccount(fromAccount);
        transactionPayment.setAmount(amount);
        transactionPayment.setDescription("Перевод средств");
        transactionRepository.save(transactionPayment);

        fromAccount.setBalance(fromResultSum);
        bankAccountRepository.save(fromAccount);

        Transaction transactionDeposit = new Transaction();
        transactionDeposit.setAccount(toAccount);
        transactionDeposit.setAmount(amount);
        transactionDeposit.setDescription("Пополнение карты");
        transactionRepository.save(transactionDeposit);

        toAccount.setBalance(toResultSum);
        bankAccountRepository.save(toAccount);

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
            fromAccount.setBalance(fromAccount.getBalance());
            bankAccountRepository.save(fromAccount);
            toAccount.setBalance(toAccount.getBalance());
            bankAccountRepository.save(toAccount);
        } else {
            emailNotificationService.sendNotification(toAccount.getUser().getEmail(), "Произошло пополнение средств");
        }
    }
}
