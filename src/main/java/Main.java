import db.hibernate.*;
import entity.hibernate.User;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO();
            BankAccountDAO accountDAO = new BankAccountDAO();
            TransactionDAO transactionDAO = new TransactionDAO();

            HibernateCreateData.initializeData();

            System.out.println("Пользователи:");
            System.out.println(userDAO.findAll());

            User updatedUser = userDAO.findById(3L);
            updatedUser.setName("Тест2");
            updatedUser.setEmail("test2@mail.ru");
            userDAO.update(updatedUser);

            System.out.println("Пользователи:");
            System.out.println(userDAO.findAll());

            userDAO.delete(3L);

            System.out.println("Пользователи:");
            System.out.println(userDAO.findAll());

            System.out.println("Счета User1:");
            System.out.println(accountDAO.findByUserId(1L));

            System.out.println("Счёт 1:");
            System.out.println(accountDAO.findById(1L));

            accountDAO.deposit(1L, BigDecimal.valueOf(600));
            System.out.println("Счёт 1:");
            System.out.println(accountDAO.findById(1L));

            accountDAO.withdraw(1L, BigDecimal.valueOf(200));
            System.out.println("Счёт 1:");
            System.out.println(accountDAO.findById(1L));

            accountDAO.transfer(1L, 2L, BigDecimal.valueOf(300));
            System.out.println("Счета User1:");
            System.out.println(accountDAO.findByUserId(1L));

            accountDAO.delete(2L);
            System.out.println("Счета User1:");
            System.out.println(accountDAO.findByUserId(1L));

            System.out.println("История Счёта 1:");
            System.out.println(transactionDAO.findByUserId(1L));

        } finally {
            HibernateUtil.closeSession();
        }
    }
}
