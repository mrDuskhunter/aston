package ru.duskhunter.aston.lesson2.service;

import ru.duskhunter.aston.lesson2.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String email, Integer age);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    void deleteUser(Long id);
    void updateUser(User user);
    void close();
}
