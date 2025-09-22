package ru.sakhapov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

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
    void createUser_ValidInput() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        userService.createUser("Name", "name@mail.com", 25);
        verify(userDao).save(captor.capture());

        User user = captor.getValue();
        assertEquals("Name", user.getName());
        assertEquals("name@mail.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @ParameterizedTest
    @CsvSource({
            " , test@mail.com, 25, Имя не может быть пустым",
            "Name, bademail, 25, Некорректный email",
            "Name, name@mail.com, -1, Возраст должен быть > 0"
    })
    void createUser_InvalidInputs(String name, String email, int age, String eMessage) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(name, email, age)
        );
        assertEquals(eMessage, e.getMessage());
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
    void getUserById_Valid() {
        User user = new User("Name", "name@mail.com", 25);
        when(userDao.findById(1L)).thenReturn(user);

        User found = userService.getUserById(1L);

        assertNotNull(found);
        assertEquals("Name", found.getName());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void getUserById_Invalid() {
        when(userDao.findById(99L)).thenReturn(null);

        User found = userService.getUserById(99L);

        assertNull(found);
        verify(userDao, times(1)).findById(99L);
    }

    @Test
    void updateUser_Valid() {
        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> email = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> age = ArgumentCaptor.forClass(Integer.class);

        userService.updateUser(1L, "Updated", "updated@mail.com", 25);

        verify(userDao).update(id.capture(), name.capture(), email.capture(), age.capture());

        assertEquals(1L, id.getValue());
        assertEquals("Updated", name.getValue());
        assertEquals("updated@mail.com", email.getValue());
        assertEquals(25, age.getValue());
    }

    @ParameterizedTest
    @CsvSource({
            "0, Name, name@mail.com, 25, Неверный id",
            "-1, Name, name@mail.com, 25, Неверный id",
            "1,  , name@mail.com, 25, Имя не может быть пустым",
            "1, Name, badEmail, 25, Некорректный email",
            "1, Name, name@mail.com, 0, Возраст должен быть > 0"
    })
    void updateUser_InvalidInputs(Long id, String name, String email, int age, String eMessage) {
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(id, name, email, age)
        );

        assertEquals(eMessage, e.getMessage());
        verify(userDao, never()).update(anyLong(), anyString(), anyString(), anyInt());
    }

    @Test
    void deleteUser_Valid() {
        userService.deleteUser(1L);

        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_Invalid() {
        doNothing().when(userDao).deleteById(99L);

        userService.deleteUser(99L);

        verify(userDao, times(1)).deleteById(99L);
    }
}
