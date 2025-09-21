package ru.sakhapov;

import jakarta.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public void save(User user) {
        Transaction tx = null;
        try (Session session = SessionFactoryMaker.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            logger.info("Пользователь сохранён: {}", user.getEmail());
        } catch (ConstraintViolationException e) {
            if (tx != null && tx.getStatus() == TransactionStatus.ACTIVE) tx.rollback();
            logger.warn("юзер с email {} уже существует", user.getEmail());
        } catch (HibernateException e) {
            if (tx != null && tx.getStatus() == TransactionStatus.ACTIVE) tx.rollback();
            logger.error("ошибка hibernate: {}", e.getMessage(), e);
        } catch (PersistenceException e) {
            if (tx != null && tx.getStatus() == TransactionStatus.ACTIVE) tx.rollback();
            logger.error("ошибка БД: {}", e.getMessage(), e);
        } catch (Exception e) {
            if (tx != null && tx.getStatus() == TransactionStatus.ACTIVE) tx.rollback();
            logger.error("неизвестная ошибка: {}", e.getMessage(), e);
        }
    }

    public User findById(Long id) {
        try (Session session = SessionFactoryMaker.getSessionFactory().openSession()) {
            return session.find(User.class, id);
        } catch (HibernateException e) {
            logger.error("Ошибка Hibernate {}: {}", id, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Неизвестная ошибка {}: {}", id, e.getMessage(), e);
        }
        return null;
    }

    public List<User> findAll() {
        try (Session session = SessionFactoryMaker.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        } catch (HibernateException e) {
            logger.error("Ошибка Hibernate при получении юзеров: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Неизвестная ошибка при получении юзеров: {}", e.getMessage(), e);
        }
        return List.of();
    }

    public void update(Long id, String name, String email, int age) {
        try (Session session = SessionFactoryMaker.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            session.createQuery(
                            "update User set name = :name, email = :email, age = :age where id = :id")
                    .setParameter("name", name)
                    .setParameter("email", email)
                    .setParameter("age", age)
                    .setParameter("id", id)
                    .executeUpdate();

            tx.commit();

            logger.info("Юзер обновлен: id = {}",id);
        }
    }

    public void deleteById(Long id) {
        Transaction tx = null;
        try (Session session = SessionFactoryMaker.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("Юзер удалён: {}", user.getEmail());
            } else {
                logger.warn("Юзер с id {} не найден", id);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null && tx.getStatus() == TransactionStatus.ACTIVE) tx.rollback();
            logger.error("Ошибка Hibernate при удалении: {}", e.getMessage(), e);
        } catch (PersistenceException e) {
            if (tx != null && tx.getStatus() == TransactionStatus.ACTIVE) tx.rollback();
            logger.error("Ошибка БД при удалении: {}", e.getMessage(), e);
        } catch (Exception e) {
            if (tx != null && tx.getStatus() == TransactionStatus.ACTIVE) tx.rollback();
            logger.error("Неизвестная ошибка при удалении: {}", e.getMessage(), e);
        }
    }
}
