package gigabank.service;

import gigabank.classifier.Currency;
import gigabank.model.BankAccount;
import gigabank.model.User;
import gigabank.repository.BankAccountRepository;
import gigabank.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class HibernateCreateData {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    public HibernateCreateData(UserRepository userRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public void initializeData() {

        User user1 = new User();
        user1.setName("Иван");
        user1.setEmail("ivan@mail.ru");
        user1.setUsername("user");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Алексей");
        user2.setEmail("alex@mail.ru");
        user2.setUsername("admin");
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("Тест");
        user3.setEmail("test@mail.ru");
        user3.setUsername("test");
        userRepository.save(user3);

        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setBalance(BigDecimal.valueOf(100.00));
        bankAccount1.setAccountNumber("11111");
        bankAccount1.setCurrency(Currency.RUB);
        bankAccount1.setUser(user1);
        bankAccountRepository.save(bankAccount1);

        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setBalance(BigDecimal.valueOf(200.00));
        bankAccount2.setAccountNumber("22222");
        bankAccount2.setCurrency(Currency.RUB);
        bankAccount2.setUser(user1);
        bankAccountRepository.save(bankAccount2);

        BankAccount bankAccount3 = new BankAccount();
        bankAccount3.setBalance(BigDecimal.valueOf(300.00));
        bankAccount3.setAccountNumber("33333");
        bankAccount3.setCurrency(Currency.RUB);
        bankAccount3.setUser(user2);
        bankAccountRepository.save(bankAccount3);

        BankAccount bankAccount4 = new BankAccount();
        bankAccount4.setBalance(BigDecimal.valueOf(400.00));
        bankAccount4.setAccountNumber("44444");
        bankAccount4.setCurrency(Currency.RUB);
        bankAccount4.setUser(user2);
        bankAccountRepository.save(bankAccount4);
    }
}