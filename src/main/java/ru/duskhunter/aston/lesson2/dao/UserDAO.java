package ru.duskhunter.aston.lesson2.dao;

import ru.duskhunter.aston.lesson2.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    void create(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
    List<String> findAllEmail();
    void close();
}
