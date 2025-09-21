package ru.sakhapov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void createUser() {
        userService.createUser("Name", "name@mail.com", 25);

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void createUser_InvalidName() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> userService.createUser("", "name@mail.com", 25)
        );

        assertEquals("Имя не может быть пустым", e.getMessage());
    }

    @Test
    void createUser_InvalidEmail() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> userService.createUser("Name", "name.com", 25)
        );

        assertEquals("Некорректный email", e.getMessage());
    }

    @Test
    void createUser_InvalidAge() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class, () -> userService.createUser("Name", "name@mail.com", -5)
        );

        assertEquals("Возраст должен быть > 0", e.getMessage());
    }

    @Test
    void getAllUsers() {
        User user1 = new User("First", "first@mail.com", 25);
        User user2 = new User("Second", "second@mail.com", 25);

        when(userDao.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userDao, times(1)).findAll();
    }

    @Test
    void getUserById() {
        User user = new User("Name", "name@mail.com", 25);
        when(userDao.findById(1L)).thenReturn(user);

        User found = userService.getUserById(1L);

        assertNotNull(found);
        assertEquals("Name", found.getName());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void updateUser() {
        userService.updateUser(1L, "Name", "name@mail.com", 25);

        verify(userDao, times(1))
                .update(1L, "Name", "name@mail.com", 25);
    }

    @Test
    void updateUser_InvalidId_ThrowsException() {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(0L, "Name", "name@mail.com", 25)
        );
        assertEquals("Неверный id", e.getMessage());
        verify(userDao, never()).update(anyLong(), anyString(), anyString(), anyInt());
    }

    @Test
    void deleteUser_CallsDaoDelete() {
        userService.deleteUser(1L);

        verify(userDao, times(1)).deleteById(1L);
    }


}
