package gigabank.service;

import gigabank.classifier.Currency;
import gigabank.model.BankAccount;
import gigabank.model.User;
import gigabank.repository.BankAccountRepository;
import gigabank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class HibernateCreateData {

    private static final int ACCOUNT_COUNT = 10;
    private static final BigDecimal START_BALANCE = BigDecimal.valueOf(1_000_000);

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public void initializeData() {
        User user1 = ensureUser("user",  "Иван",     "ivan@mail.ru");
        User user2 = ensureUser("admin", "Алексей",  "alex@mail.ru");
        User user3 = ensureUser("test",  "Тест",     "test@mail.ru");

        ensureAccount("11111", BigDecimal.valueOf(100.00), user1);
        ensureAccount("22222", BigDecimal.valueOf(200.00), user1);
        ensureAccount("33333", BigDecimal.valueOf(300.00), user2);
        ensureAccount("44444", BigDecimal.valueOf(400.00), user2);

        for (int i = 0; i < ACCOUNT_COUNT; i++) {
            ensureAccount("ACCOUNT_" + i, START_BALANCE, user3);
        }
    }

    private User ensureUser(String username, String name, String email) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(username);
                    u.setName(name);
                    u.setEmail(email);
                    return userRepository.save(u);
                });
    }

    private BankAccount ensureAccount(String accountNumber,
                                      BigDecimal balance,
                                      User owner) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseGet(() -> {
                    BankAccount a = new BankAccount();
                    a.setAccountNumber(accountNumber);
                    a.setBalance(balance);
                    a.setCurrency(Currency.RUB);
                    a.setUser(owner);
                    return bankAccountRepository.save(a);
                });
    }
}