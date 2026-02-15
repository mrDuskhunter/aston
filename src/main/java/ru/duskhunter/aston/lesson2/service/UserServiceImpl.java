package ru.duskhunter.aston.lesson2.service;

import ru.duskhunter.aston.lesson2.dao.UserDAO;
import ru.duskhunter.aston.lesson2.dao.UserDAOImpl;
import ru.duskhunter.aston.lesson2.entity.User;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    // Конструктор для тестов (внедрение зависимости)
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exist");
        }
        User user = new User(name, email, age);
        userDAO.create(user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userDAO.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userDAO.delete(id);
    }

    @Override
    public void updateUser(User user) {
        userDAO.update(user);
    }

    private boolean existsByEmail(String email) {
        return userDAO.findAllEmail()
                .stream()
                .anyMatch(e -> e.equalsIgnoreCase(email));
    }

    @Override
    public void close() {
        userDAO.close();
    }
}
