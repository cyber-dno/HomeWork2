package db.hibernate;

import entity.hibernate.BankAccount;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class BankAccountDAO {
    public void save(BankAccount account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(account);
            transaction.commit();
        }
    }

    public BankAccount findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(BankAccount.class, id);
        }
    }

    public List<BankAccount> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT a FROM BankAccount a WHERE a.user.id = :userId",
                            BankAccount.class
                    ).setParameter("userId", userId)
                    .list();
        }
    }

    public void deposit(Long accountId, BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BankAccount account = session.find(BankAccount.class, accountId);
            if (account != null) {
                Transaction transaction = session.beginTransaction();

                account.setBalance(account.getBalance().add(amount));

                entity.hibernate.Transaction transactionEntity = new entity.hibernate.Transaction();
                transactionEntity.setAccount(account);
                transactionEntity.setAmount(amount);
                transactionEntity.setDescription("Deposit");
                session.persist(transactionEntity);

                transaction.commit();
            }
        }
    }

    public void withdraw(Long accountId, BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BankAccount account = session.find(BankAccount.class, accountId);
            if (account != null) {
                Transaction transaction = session.beginTransaction();

                account.setBalance(account.getBalance().subtract(amount));

                entity.hibernate.Transaction transactionEntity = new entity.hibernate.Transaction();
                transactionEntity.setAccount(account);
                transactionEntity.setAmount(amount);
                transactionEntity.setDescription("Withdraw");
                session.persist(transactionEntity);

                if (account.getBalance().compareTo(BigDecimal.ZERO) < 0)
                    transaction.rollback();
                else
                    transaction.commit();
            }
        }
    }

    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            BankAccount fromAccount = session.find(BankAccount.class, fromAccountId);
            BankAccount toAccount = session.find(BankAccount.class, toAccountId);
            if (fromAccount != null && toAccount != null) {
                Transaction transaction = session.beginTransaction();

                fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
                toAccount.setBalance(toAccount.getBalance().add(amount));

                entity.hibernate.Transaction transactionEntity = new entity.hibernate.Transaction();
                transactionEntity.setAccount(fromAccount);
                transactionEntity.setAmount(amount);
                transactionEntity.setDescription("Transfer");
                session.persist(transactionEntity);

                transactionEntity = new entity.hibernate.Transaction();
                transactionEntity.setAccount(toAccount);
                transactionEntity.setAmount(amount);
                transactionEntity.setDescription("Transfer");
                session.persist(transactionEntity);

                if (fromAccount.getBalance().compareTo(BigDecimal.ZERO) < 0)
                    transaction.rollback();
                else
                    transaction.commit();
            }
        }
    }

    public void delete(Long accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            BankAccount account = findById(accountId);
            if (account != null) {
                session.remove(account);
            }

            transaction.commit();
        }
    }
}
