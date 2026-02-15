package ru.duskhunter.aston.lesson2.service;

import lombok.extern.log4j.Log4j2;
import ru.duskhunter.aston.lesson2.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Log4j2
public class ConsoleUI {
    private final UserService userService;
    private final Scanner scanner;

    public ConsoleUI() {
        this.userService = new UserServiceImpl();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createUser();
                case "2" -> findAllUsers();
                case "3" -> findUserById();
                case "4" -> updateUser();
                case "5" -> deleteUser();
                case "0" -> {
                    System.out.println("Goodbye!");
                    scanner.close();
                    log.info("Scanner is close");
                    userService.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private void printMenu() {
        System.out.println("\n=== USER MANAGEMENT SYSTEM ===");
        System.out.println("1. Create User");
        System.out.println("2. List All Users");
        System.out.println("3. Find User by ID");
        System.out.println("4. Update User");
        System.out.println("5. Delete User");
        System.out.println("0. Exit");
        System.out.println("\nEnter your choice: ");
    }

    private void createUser() {
        System.out.println("\n--- CREATE NEW USER ---");
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Invalid name format. This field cannot be empty");
            }

            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                throw new IllegalArgumentException("Invalid email format. This field cannot be empty");
            }

            System.out.print("Enter age: ");
            int age = Integer.parseInt(scanner.nextLine());

            if (age < 1 || age > 150) {
                throw new IllegalArgumentException("Invalid age format. This field cannot be less 1 and more 150");
            }

            User user = userService.createUser(name, email, age);
            System.out.println("User created successfully: " + user);
        } catch (NumberFormatException e) {
            System.out.println("Invalid age format. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            log.error("Error in createUser", e);
        }
    }

    private void findAllUsers() {
        System.out.println("\n--- ALL USERS ---");
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                users.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error fetching users: " + e.getMessage());
            log.error("Error in findAllUsers", e);
        }
    }

    private void findUserById() {
        System.out.println("\n--- FIND USER BY ID ---");
        try {
            System.out.print("Enter user ID: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                System.out.println("User found: " + userOpt.get());
            } else {
                System.out.println("User not found with ID: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error finding user: " + e.getMessage());
            log.error("Error in findUserById", e);
        }
    }

    private void updateUser() {
        System.out.println("\n--- UPDATE USER ---");
        try {
            System.out.print("Enter user ID to update: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            Optional<User> existingUser = userService.getUserById(id);
            if (existingUser.isEmpty()) {
                System.out.println("User not found with ID: " + id);
                return;
            }

            User user = existingUser.get();

            System.out.println("Current user data: " + existingUser.get());

            System.out.print("Enter new name (press Enter to keep current): ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                user.setName(name);
            }

            System.out.print("Enter new age (press Enter to keep current): ");
            String ageStr = scanner.nextLine().trim();
            if (!ageStr.trim().isEmpty()) {
                try {
                    int age = Integer.parseInt(ageStr);

                    if (age < 1 || age > 150) {
                        throw new IllegalArgumentException("Invalid age format. This field cannot be less 1 and more 150");
                    }

                    user.setAge(age);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid age format. Keeping current age.");
                }
            }

            userService.updateUser(user);
            System.out.println("User updated successfully: " + user);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            log.error("Error in updateUser", e);
        }
    }

    private void deleteUser() {
        System.out.println("\n--- DELETE USER ---");
        try {
            System.out.print("Enter user ID to delete: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            userService.deleteUser(id);
            System.out.println("User deleted successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
            log.error("Error in deleteUser", e);
        }
    }

}
