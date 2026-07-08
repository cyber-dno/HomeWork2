package service;

import db.DBManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class PhantomRead {
    private static final DBManager dbManager = DBManager.getInstance();
    private static final String ACCOUNT_NUMBER = "11111";

    public static void transactionRead(Connection connection) {
        try {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);

            System.out.println(dbManager.getBankAccount(ACCOUNT_NUMBER, connection));
            Thread.sleep(1000);
            System.out.println(dbManager.getBankAccount(ACCOUNT_NUMBER, connection));

            connection.commit();
        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void transactionWrite(Connection connection) {
        dbManager.updateBalance(ACCOUNT_NUMBER, BigDecimal.ZERO, connection);
    }
}
