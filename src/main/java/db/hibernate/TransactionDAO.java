package db.hibernate;

import entity.hibernate.Transaction;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionDAO {
    public void save(Transaction transaction) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            org.hibernate.Transaction sessionTransaction = session.beginTransaction();
            session.persist(transaction);
            sessionTransaction.commit();
        }
    }

    public List<Transaction> findByAccountId(Long accountId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT t FROM Transaction t " +
                                    "WHERE t.account.id = :accountId",
                            Transaction.class)
                    .setParameter("accountId", accountId)
                    .getResultList();
        }
    }

    public List<Transaction> findByUserId(Long userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT t FROM Transaction t " +
                                    "WHERE t.account.user.id = :userId",
                            Transaction.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }

    public List<Transaction> findByTimestamp(LocalDateTime timestamp) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Transaction WHERE timestamp = :timestamp",
                            Transaction.class)
                    .setParameter("timestamp", timestamp)
                    .getResultList();
        }
    }

    public List<Transaction> findByAmount(BigDecimal amount) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Transaction WHERE amount = :amount",
                            Transaction.class)
                    .setParameter("amount", amount)
                    .getResultList();
        }
    }
}