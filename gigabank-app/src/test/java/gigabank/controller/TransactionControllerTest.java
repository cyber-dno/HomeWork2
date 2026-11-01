package gigabank.controller;

import gigabank.exception.AccountNoFundsException;
import gigabank.repository.BankAccountRepository;
import gigabank.repository.OutboxMessageRepository;
import gigabank.repository.TransactionRepository;
import gigabank.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class TransactionControllerTest {

    private static final String FROM = "11111";
    private static final String TO   = "22222";

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @BeforeEach
    void cleanOutbox() {
        outboxMessageRepository.deleteAll();
    }

    // Провальная отправка
    @Test
    void transactionAndOutboxFail() {
        BigDecimal fromBefore = balanceOf(FROM);
        BigDecimal toBefore   = balanceOf(TO);
        BigDecimal sum = new BigDecimal("10000.00");

        assertEquals(0, outboxMessageRepository.countOutboxMessagesByProcessed(false));

        assertThrows(AccountNoFundsException.class, () -> bankAccountService.transfer(FROM, TO, sum));
        assertEquals(0, outboxMessageRepository.countOutboxMessagesByProcessed(false));

        BigDecimal fromAfter = balanceOf(FROM);
        BigDecimal toAfter   = balanceOf(TO);

        assertEquals(fromAfter,  fromBefore);
        assertEquals(toAfter,  toBefore);
    }

    // Успешная отправка
    @Test
    void transactionAndOutboxSuccess() {
        BigDecimal fromBefore = balanceOf(FROM);
        BigDecimal toBefore   = balanceOf(TO);
        BigDecimal sum = new BigDecimal("10.00");

        assertEquals(0, outboxMessageRepository.countOutboxMessagesByProcessed(false));

        bankAccountService.transfer(FROM, TO, sum);

        assertEquals(2, outboxMessageRepository.countOutboxMessagesByProcessed(false));

        BigDecimal fromAfter = balanceOf(FROM);
        BigDecimal toAfter   = balanceOf(TO);

        assertEquals(fromAfter,  fromBefore.subtract(sum));
        assertEquals(toAfter,  toBefore.add(sum));
    }

    private BigDecimal balanceOf(String accountNumber) {
        return bankAccountRepository.getBankAccountByAccountNumber(accountNumber).getBalance();
    }
}
