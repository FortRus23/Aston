package ru.sakhapov;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        SessionFactoryMaker.init(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        userDao = new UserDao();
    }

    @BeforeEach
    void clean() {
        try (Session session = SessionFactoryMaker.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            tx.commit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Сохраняем юзера и находим его по id")
    void shouldSaveAndFindById() {
        User user = new User("Name", "name@mail.com", 22);
        userDao.save(user);

        User found = userDao.findById(user.getId());

        assertNotNull(found, "юзер должен быть найден");
        assertEquals("Name", found.getName(), "имя должно совпадать");
        assertEquals("name@mail.com", found.getEmail(), "email должен совпадать");
        assertEquals(22, found.getAge(), "возраст должен совпадать");
    }

    @Test
    @Order(2)
    @DisplayName("Поиск несуществующего юзера возвращает null")
    void shouldReturnNullWhenIdNotExists() {
        User user = userDao.findById(100L);
        assertNull(user, "юзер не должен существовать");
    }

    @Test
    @Order(3)
    @DisplayName("Находим всех юзеров")
    void shouldFindAllUsers() {
        userDao.save(new User("Name", "name@mail.com", 22));
        userDao.save(new User("Second Name", "name.second@mail.com", 23));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size(), "Должно быть 2 юзера в базе");
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Name")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Second Name")));
    }

    @Test
    @Order(4)
    @DisplayName("Обновляем юзера")
    void shouldUpdateUser() {
        User user = new User("Name", "name@mail.com", 22);
        userDao.save(user);

        userDao.update(user.getId(), "Name Updated", "name.updated@mail.com", 33);

        User updated = userDao.findById(user.getId());

        assertNotNull(updated);
        assertEquals("Name Updated", updated.getName());
        assertEquals("name.updated@mail.com", updated.getEmail());
        assertEquals(33, updated.getAge());
    }

    @Test
    @Order(5)
    @DisplayName("Удаляем юзера")
    void shouldDeleteUser() {
        User user = new User("Name", "name@mail.com", 22);
        userDao.save(user);

        userDao.deleteById(user.getId());
        User deleted = userDao.findById(user.getId());

        assertNull(deleted);
    }
}
