import db.DBConnectionManager;
import db.DBCreateTables;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import service.PhantomRead;
import service.db.BankAccountServiceBD;

import java.math.BigDecimal;
import java.sql.Connection;

@Configuration
@ComponentScan(basePackages = {"service"})
@PropertySource("classpath:application.properties")
public class Main {

    public static void main(String[] args) {
        DBCreateTables.initializeDatabase();

        Connection connection = DBConnectionManager.getInstance().get();
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class)) {

            BankAccountServiceBD bankAccountService = context.getBean(BankAccountServiceBD.class);
            String accountNumber = "11111";
            String additionalAccount = "33333";

            System.out.println(bankAccountService.findAccountByNumber(accountNumber, connection));
            System.out.println(bankAccountService.findAccountByNumber(additionalAccount, connection));
            System.out.println();

            bankAccountService.withdraw(accountNumber, BigDecimal.valueOf(200.0), connection);
            System.out.println();
            bankAccountService.deposit(accountNumber, BigDecimal.valueOf(1000.0), connection);
            System.out.println();
            bankAccountService.withdraw(accountNumber, BigDecimal.valueOf(200.0), connection);
            System.out.println();
            bankAccountService.transfer(accountNumber, additionalAccount, BigDecimal.valueOf(500.0), connection);
            System.out.println();

            System.out.println(bankAccountService.findAccountByNumber(accountNumber, connection));
            System.out.println(bankAccountService.findAccountByNumber(additionalAccount, connection));

            System.out.println();
            System.out.println(bankAccountService.getAccountTransactions(accountNumber, connection));
            System.out.println(bankAccountService.getAccountTransactions(additionalAccount, connection));
            System.out.println();

            // Phantom Read
            new Thread(() -> PhantomRead.transactionRead(connection)).start();
            new Thread(() -> PhantomRead.transactionWrite(connection)).start();

        } finally {
            DBConnectionManager.getInstance().disconnect(connection);
        }
    }
}
