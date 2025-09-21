package ru.sakhapov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {
    private final UserDao userDao;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(String name, String email, int age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Возраст должен быть > 0");
        }

        User user = new User(name, email, age);
        userDao.save(user);
        logger.info("Создан юзер: {}", email);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User getUserById(Long id) {
        return userDao.findById(id);
    }

    public void updateUser(Long id, String name, String email, int age) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Неверный id");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (age <= 0) {
            throw new IllegalArgumentException("Возраст должен быть > 0");
        }

        userDao.update(id, name, email, age);
        logger.info("Обновлен юзер id={} email={}", id, email);
    }

    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }
}
