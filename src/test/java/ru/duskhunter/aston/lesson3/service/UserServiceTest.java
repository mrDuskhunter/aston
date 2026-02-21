package ru.duskhunter.aston.lesson3.service;

/*
    1. create user
    1.1. when email not exist -> dao.existsByEmail(), dao.create(), newUser == return user
    1.2. when email exist -> throw new IllegalArgumentException("User with this email already exist");
 */


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.duskhunter.aston.lesson2.dao.UserDAO;
import ru.duskhunter.aston.lesson2.entity.User;
import ru.duskhunter.aston.lesson2.service.UserService;
import ru.duskhunter.aston.lesson2.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {
    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp(){
        userService = new UserServiceImpl(userDAO);
    }

    @Test
    void createUserWhenEmailNotExist() {
        String name = "Alex";
        String email = "alex@mail.ru";
        Integer age = 33;

        when(userDAO.findAllEmail()).thenReturn(List.of("another@mail.ru"));

        User result = userService.createUser(name, email, age);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(age, result.getAge());
        verify(userDAO, times(1)).create(any(User.class));
        verify(userDAO, times(1)).findAllEmail();
    }

    @Test
    void createUserWhenEmailExist() {
        String name = "Alex";
        String email = "alex@mail.ru";
        Integer age = 33;

        when(userDAO.findAllEmail()).thenReturn(List.of(email));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(name, email, age));

        assertEquals("User with this email already exist", exception.getMessage());
        verify(userDAO,never()).create(any(User.class));
    }

    @Test
    void TestIgnoreCaseWhenCheckEmailExist() {
        String name = "Alex";
        String email = "alex@mail.ru";
        Integer age = 33;

        when(userDAO.findAllEmail()).thenReturn(List.of("AleX@mail.ru"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(name, email, age));

        assertEquals("User with this email already exist", exception.getMessage());
        verify(userDAO,never()).create(any(User.class));
    }
}