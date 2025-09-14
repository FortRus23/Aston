package ru.sakhapov;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final UserDao dao = new UserDao();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        while (true) {
            System.out.println("----------------------");
            System.out.println("1. Создать юзера");
            System.out.println("2. Показать всех юзеров");
            System.out.println("3. Найти юзера по id");
            System.out.println("4. Обновить юзера");
            System.out.println("5. Удалить юзера");
            System.out.println("0. Выход");
            System.out.print("Выберите: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createUser();
                case 2 -> listUsers();
                case 3 -> getUserById();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> System.exit(0);
                default -> System.out.println("неверный выбор");
            }
        }
    }

    private static void createUser() {

        try {
            System.out.print("Имя: ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Имя не может быть пустым");
            }

            System.out.print("Email: ");
            String email = scanner.nextLine();
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException("Некорректный email");
            }

            System.out.print("Возраст: ");
            int age = scanner.nextInt();
            scanner.nextLine();
            if (age <= 0) {
                throw new IllegalArgumentException("Возраст должен быть больше 0");
            }

            dao.save(new User(name, email, age));
        } catch (InputMismatchException e) {
            System.out.println("Возраст должен быть числом!");
            scanner.nextLine();
            logger.warn("Некорректный ввод возраста", e);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
            logger.warn("Некорректный ввод юзера: {}", e.getMessage());
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка: " + e.getMessage());
            logger.error("Ошибка при создании юзера", e);
        }
    }

    private static void listUsers() {
        List<User> users = dao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пуст");
        } else {
            users.forEach(u ->
                    System.out.println("id: " + u.getId() + ", " + u.getName() + ", " + u.getEmail() + ", " + u.getAge())
            );
        }
    }

    private static void getUserById() {
        try {
            System.out.print("Введите ID: ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            User user = dao.findById(id);
            if (user != null) {
                System.out.println("id: " + user.getId() + ", " + user.getName() + ", " + user.getEmail() + ", " + user.getAge());
            } else {
                System.out.println("Юзер с таким id не найден");
            }
        } catch (InputMismatchException e) {
            System.out.println("id должен быть числом");
            scanner.nextLine();
            logger.warn("Неверный ввод id", e);
        }
    }

    private static void updateUser() {
        try {
            System.out.print("Введите id для обновления: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            User user = dao.findById(id);
            if (user == null) {
                System.out.println("Юзер не найден");
                return;
            }

            System.out.print("Имя: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) user.setName(name);

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) {
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    System.out.println("Некорректный email");
                } else {
                    user.setEmail(email);
                }
            }

            System.out.print("Возраст: ");
            String ageStr = scanner.nextLine().trim();
            if (!ageStr.isEmpty()) {
                try {
                    int age = Integer.parseInt(ageStr);
                    if (age > 0) user.setAge(age);
                    else System.out.println("Возраст должен быть > 0");
                } catch (NumberFormatException ex) {
                    System.out.println("Неверный возраст");
                }
            }

            dao.update(user);
            logger.info("Обновлен юзер: {}", user.getEmail());

        } catch (InputMismatchException e) {
            System.out.println("Неверный ввод");
            scanner.nextLine();
            logger.warn("Ошибка ввода при обновлении", e);
        } catch (Exception e) {
            System.out.println("ошибка: " + e.getMessage());
            logger.error("Ошибка при обновлении юзера", e);
        }
    }

    private static void deleteUser() {
        try {
            System.out.print("Введите id юзера для удаления: ");
            Long id = scanner.nextLong();
            scanner.nextLine();
            dao.deleteById(id);
        } catch (InputMismatchException e) {
            System.out.println("id должен быть числом");
            scanner.nextLine();
            logger.warn("Неверный ввод id при удалении", e);
        } catch (Exception e) {
            System.out.println("Ошибка при удалении: " + e.getMessage());
            logger.error("Ошибка при удалении юзера", e);
        }
    }
}
