package ru.sakhapov;


import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final UserDao userDao = new UserDao();
    private static final UserService userService = new UserService(userDao);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Создать юзера");
            System.out.println("2. Показать всех юзеров");
            System.out.println("3. Найти юзера по id");
            System.out.println("4. Обновить юзера");
            System.out.println("5. Удалить юзера");
            System.out.println("0. Выход");

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
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Возраст: ");
            int age = scanner.nextInt();
            scanner.nextLine();

            userService.createUser(name, email, age);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void listUsers() {
        userService.getAllUsers().forEach(u ->
                System.out.println(u.getId() + " " + u.getName() + " " + u.getEmail() + " " + u.getAge()));
    }

    private static void getUserById() {
        System.out.print("Введите id: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        User user = userService.getUserById(id);
        System.out.println(user != null ? user : "Не найден");
    }

    private static void updateUser() {
        try {
            System.out.print("Введите id для обновления: ");
            Long id = scanner.nextLong();
            scanner.nextLine();

            System.out.print("Новое имя: ");
            String name = scanner.nextLine().trim();

            System.out.print("Новый email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Новый возраст: ");
            int age = scanner.nextInt();
            scanner.nextLine();

            userService.updateUser(id, name, email, age);
            System.out.println("Юзер обновлен успешно");

        } catch (InputMismatchException e) {
            System.out.println("Ошибка: возраст и id должны быть числами");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.print("Введите id для удаления: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        userService.deleteUser(id);
    }
}
