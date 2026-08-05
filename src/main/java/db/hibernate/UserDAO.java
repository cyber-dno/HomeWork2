package db.hibernate;

import org.hibernate.Transaction;
import entity.hibernate.User;
import org.hibernate.Session;

import java.util.List;

public class UserDAO {
    public void save(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        }
    }

    public User findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(User.class, id);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public void update(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User userOld = session.find(User.class, user.getId());
            if (userOld != null) {
                userOld.setName(user.getName());
                userOld.setEmail(user.getEmail());
            }

            transaction.commit();
        }
    }

    public void delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            User user = findById(id);
            if (user != null) {
                session.remove(user);
            }

            transaction.commit();
        }
    }
}
