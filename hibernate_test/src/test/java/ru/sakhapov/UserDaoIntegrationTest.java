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
    void shouldSaveAndFindById() {
        User user = new User("Name", "name@mail.com", 22);
        userDao.save(user);

        User found = userDao.findById(user.getId());

        assertNotNull(found);
        assertEquals("Name", found.getName());
        assertEquals("name@mail.com", found.getEmail());
    }

    @Test
    void testFindByIdNotExists() {
        User user = userDao.findById(100L);
        assertNull(user);
    }

    @Test
    void testFindAll() {
        userDao.save(new User("Name", "name@mail.com", 22));
        userDao.save(new User("Second Name", "name.second@mail.com", 23));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Name")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Second Name")));
    }

    @Test
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
    void shoudDeleteUser() {
        User user = new User("Name", "name@mail.com", 22);
        userDao.save(user);

        userDao.deleteById(user.getId());
        User deleted = userDao.findById(user.getId());

        assertNull(deleted);
    }
}
