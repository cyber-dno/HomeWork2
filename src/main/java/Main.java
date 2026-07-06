import db.DBCreateTables;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import service.db.BankAccountService;

import java.math.BigDecimal;

@Configuration
@ComponentScan(basePackages = {"service"})
@PropertySource("classpath:application.properties")
public class Main{

    public static void main( String[] args ) {
        DBCreateTables.initializeDatabase();

        try (AnnotationConfigApplicationContext  context = new AnnotationConfigApplicationContext(Main.class)) {
            BankAccountService bankAccountService = context.getBean(BankAccountService.class);
            String accountNumber = "11111";
            String additionalAccount = "33333";

            System.out.println(bankAccountService.findAccountByNumber(accountNumber));
            System.out.println(bankAccountService.findAccountByNumber(additionalAccount));

            bankAccountService.withdraw(accountNumber, BigDecimal.valueOf(200.0));
            bankAccountService.deposit(accountNumber, BigDecimal.valueOf(1000.0));
            bankAccountService.withdraw(accountNumber, BigDecimal.valueOf(200.0));
            bankAccountService.transfer(accountNumber, additionalAccount, BigDecimal.valueOf(500.0));

            System.out.println(bankAccountService.findAccountByNumber(accountNumber));
            System.out.println(bankAccountService.findAccountByNumber(additionalAccount));

            System.out.println(bankAccountService.getAccountTransactions(accountNumber));
            System.out.println(bankAccountService.getAccountTransactions(additionalAccount));
        }
    }
}
