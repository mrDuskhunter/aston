package ru.duskhunter.aston.lesson2.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;


import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import ru.duskhunter.aston.lesson2.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/*
    testCase:
     1. save user
        1.1. positive case -> create index, create column bd, equals attributes, create createdAt - done
        1.2. user == null -> throw new IllegalArgumentException("User cannot be null"); - done
        1.3. user.id != null -> throw new IllegalArgumentException("New user has id"); - done
        1.4. name is null -> throw new ConstraintViolationException "Имя обязательно и должно содержать символы" - done
        1.5. name > 100 symbols -> throw new ConstraintViolationException "Максимальная длина имени — 100 символов" - done
        1.6. name > is empty or has only space(""/"  ") -> throw new ConstraintViolationException "Имя обязательно и должно содержать символы" - done/done
        1.7. email is null -> throw new ConstraintViolationException "Email обязателен и должен содержать символы" - done
        1.8. email > 100 symbols -> throw new ConstraintViolationException "Максимальная длина email — 100 символов" - done
        1.9. email > is empty or has only space(""/"  ") -> throw new ConstraintViolationException "Email обязателен и должен содержать символы" - done/done
        1.10. age < 0 -> throw new ConstraintViolationException "Возраст должен быть больше 0" - done
        1.11. age > 150 -> throw new ConstraintViolationException "Возраст должен быть меньше или равен 150" - done
        1.12. age == 150 -> create user - done
     2. getUser
        2.1. positive case - done
        2.2. find(id) == null -> IllegalArgumentException("id cannot be null") - done
        2.3 User not found -> optional with null (new Optional<>(null);) - done
     3. get List Users - positive case - done
     4. get List Users email - positive case - done
     5. update user
        5.1. positive case - done
        5.2 user == null -> throw new IllegalArgumentException("User cannot be null"); - done
        5.3. during the update, the user disappears from db -> throw new RuntimeException("User not found with id: " + id); step: - done
            5.3.1. user find, setName
            5.3.2. delete in db user with this id
            5.3.3. update() -> throw new IllegalArgumentException("User not found with id: " + id);
        5.4 user.id is null -> throw new IllegalArgumentException("User id cannot be null"); - done
     6. delete user - done
        6.1. user == null -> throw new IllegalArgumentException("User not found with id: " + id); - done
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private UserDAO userDAO;
    private EntityManagerFactory factory;

    @BeforeAll
    void setUp() {
        userDAO = new UserDAOImpl();
        factory = Persistence.createEntityManagerFactory("db-univer");
    }

    @BeforeEach
    void cleanDB() {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.createQuery("DELETE FROM User").executeUpdate();
        transaction.commit();
        entityManager.close();
    }

    @AfterAll
    void close() {
        if (factory.isOpen()) {
            factory.close();
        }
    }

    @Test
    void testSaveUser() {
        User user = new User("Alexey", "alexey@mail.ru", 33);
        userDAO.create(user);

        List<User> users = userDAO.findAll();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertEquals(user.getName(), users.get(0).getName());
        assertEquals(user.getEmail(), users.get(0).getEmail());
        assertEquals(user.getAge(), users.get(0).getAge());
    }

    @Test
    void testSaveUserWhenUserIsNull() {
        //user == null -> throw new IllegalArgumentException("User cannot be null");
        User user = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDAO.create(user));
        assertEquals("User cannot be null", exception.getMessage());

        List<User> users = userDAO.findAll();
        assertEquals(0, users.size());
    }

    @Test
    void testSaveUserWhenIdIsNotNull() {
        //user.id != null -> throw new IllegalArgumentException("New user has id");
        userDAO.create(new User("Alexey", "alexey@mail.ru", 33));
        List<User> users = userDAO.findAll();
        userDAO.delete(users.get(0).getId());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDAO.create(users.get(0)));
        assertEquals("New user has id", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenNameIsNull() {
//      name is null -> throw new ConstraintViolationException "Имя обязательно и должно содержать символы"

        User user = new User(null, "alexey@mail.ru", 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Имя обязательно и должно содержать символы", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenNameIsEmpty() {
//      1.6. name > is empty or has only space(""/"  ") -> throw new ConstraintViolationException "Имя обязательно и должно содержать символы"

        User user = new User("", "alexey@mail.ru", 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Имя обязательно и должно содержать символы", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenNameIsSpace() {
//      1.6. name > is empty or has only space(""/"  ") -> throw new ConstraintViolationException "Имя обязательно и должно содержать символы"

        User user = new User(" ", "alexey@mail.ru", 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Имя обязательно и должно содержать символы", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenLengthNameIsMoreThen100() {
//      1.5. name > 100 symbols -> throw new ConstraintViolationException "Максимальная длина имени — 100 символов."
        int length = 101;
        StringBuilder name = new StringBuilder(length);

        name.append("x".repeat(length));

        assertEquals(101,name.length());

        User user = new User(name.toString(), "alexey@mail.ru", 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Максимальная длина имени — 100 символов", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenEmailIsNull() {
        //email is null -> throw new ConstraintViolationException "Email обязателен и должен содержать символы"
        User user = new User("Alexey", null, 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Email обязателен и должен содержать символы", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenEmailIsEmpty() {
//      1.9. email > is empty or has only space(""/"  ") -> throw new ConstraintViolationException "Email обязателен и должен содержать символы"

        User user = new User("Alex", "", 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Email обязателен и должен содержать символы", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenEmailIsSpace() {
//      1.9. email > is empty or has only space(""/"  ") -> throw new ConstraintViolationException "Email обязателен и должен содержать символы"

        User user = new User("Alex", " ", 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Email обязателен и должен содержать символы", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenLengthEmailIsMoreThen100() {
//      1.8. email > 100 symbols -> throw new ConstraintViolationException "Максимальная длина email — 100 символов"
        int length = 101;
        StringBuilder email = new StringBuilder(length);

        email.append("x".repeat(length));

        assertEquals(101,email.length());

        User user = new User("Alex", email.toString(), 33);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Максимальная длина email — 100 символов", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenAgeMoreThen150() {
//            1.11. age > 150 -> throw new ConstraintViolationException "Возраст должен быть меньше или равен 150"

        User user = new User("Alex", "alexey@mail.ru", 151);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Возраст должен быть меньше или равен 150", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testSaveUserWhenAgeEquals150() {
//            1.11. age > 150 -> throw new ConstraintViolationException "Возраст должен быть меньше или равен 150" - positive case -> should create user

        User user = new User("Alex", "alexey@mail.ru", 150);

        userDAO.create(user);

        List<User> users = userDAO.findAll();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getAge(), users.get(0).getAge());
    }

    @Test
    void testSaveUserWhenAgeLessThen1() {
//            1.10. age < 0 -> throw new ConstraintViolationException "Возраст должен быть больше 0"

        User user = new User("Alex", "alexey@mail.ru", 0);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> userDAO.create(user));
        assertEquals("Возраст должен быть больше 0", exception.getMessage());

        assertEquals(0, userDAO.findAll().size());
    }

    @Test
    void testFindUserAndFindAll() {
        initListUsers().forEach(user -> userDAO.create(user));

        List<User> query = userDAO.findAll();
        assertNotNull(query);
        assertEquals(6, query.size());

        User expectedUser = query.get(0);
        long id = expectedUser.getId();

        Optional<User> optionalUser = userDAO.findById(id);

        assertTrue(optionalUser.isPresent());

        User actualUser = optionalUser.get();

        assertEquals(id, actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getAge(), actualUser.getAge());
    }

    @Test
    void testFindUserWhenUserIsNull() {
        //id == null -> IllegalArgumentException("id cannot be null")
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDAO.findById(null));
        assertEquals("id cannot be null", exception.getMessage());
    }

    @Test
    void testFindUserWhenUserNotFound() {
        initListUsers().forEach(user -> userDAO.create(user));
        long maxId = userDAO.findAll().stream().map(User::getId).max(Long::compareTo).get();

        //User not found -> optional with null (new Optional<>(null);)
        Optional<User> response = userDAO.findById(maxId + 1);
        assertTrue(response.isEmpty());
    }

    @Test
    void testDeleteUser() {
        initListUsers().forEach(user -> userDAO.create(user));

        List<User> query = userDAO.findAll();
        assertNotNull(query);
        assertEquals(6, query.size());

        User expectedUser = query.get(0);

        assertTrue(userDAO.findById(expectedUser.getId()).isPresent());

        userDAO.delete(expectedUser.getId());

        assertTrue(userDAO.findById(expectedUser.getId()).isEmpty());
    }

    @Test
    void testDeleteUserWhenUserIsNull() {
        initListUsers().forEach(user -> userDAO.create(user));

        List<User> usersDB = userDAO.findAll();

        long maxId = usersDB.stream().map(User::getId).max(Long::compareTo).get();

        //user == null -> (id > maxId) -> throw new IllegalArgumentException("User not found with id: " + id);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDAO.delete(maxId + 1));
        assertEquals("User not found with id: " + (maxId + 1), exception.getMessage());

        assertEquals(usersDB.size(), userDAO.findAll().size());
    }

    @Test
    void testUpdateUser() {
        initListUsers().forEach(user -> userDAO.create(user));

        List<User> query = userDAO.findAll();
        assertNotNull(query);
        assertEquals(6, query.size());

        User legasyUser = query.get(0);
        long id = legasyUser.getId();
        String email = legasyUser.getEmail();
        LocalDateTime createAt = legasyUser.getCreatedAt();

        String newName = "Boris-britva";
        int newAge = 48;

        assertFalse(legasyUser.getName().equalsIgnoreCase(newName));
        assertNotEquals(legasyUser.getAge(), newAge);

        legasyUser.setAge(newAge);
        legasyUser.setName(newName);
        userDAO.update(legasyUser);

        Optional<User> optionalUser = userDAO.findById(id);
        assertTrue(optionalUser.isPresent());

        User actualUser = optionalUser.get();

        assertEquals(id, actualUser.getId());
        assertEquals(email, actualUser.getEmail());
        assertEquals(createAt, actualUser.getCreatedAt());
        assertEquals(newName, actualUser.getName());
        assertEquals(newAge, actualUser.getAge());
    }


    @Test
    void testUpdateUserWhenUserIsNull() {
        User user = null;
        //user == null -> throw new IllegalArgumentException("User cannot be null");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDAO.update(user));
        assertEquals("User cannot be null", exception.getMessage());
    }

    @Test
    void testUpdateUserWhenDuringUpdateUserDisappearsFromDB() {
        initListUsers().forEach(user -> userDAO.create(user));

        List<User> query = userDAO.findAll();
        assertNotNull(query);
        assertEquals(6, query.size());

        User legasyUser = query.get(0);
        long id = legasyUser.getId();

        String newName = "Boris-britva";

        assertFalse(legasyUser.getName().equalsIgnoreCase(newName));

        /*during the update, the user disappears from db -> throw new RuntimeException("User not found with id: " + id);
            step:
                1. setName User
                2. delete in db user with this id
                3. update() -> throw new RuntimeException("User not found with id: " + id);
         */

        legasyUser.setName(newName);
        userDAO.delete(id);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDAO.update(legasyUser));
        assertEquals("User not found with id: " + id, exception.getMessage());
    }


    @Test
    void testUpdateUserWhenUserIdIsNull() {
        //5.4 user.id is null -> throw new IllegalArgumentException("User id cannot be null");
        User user = new User("Alex", "alexey@mail.ru", 1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userDAO.update(user));
        assertEquals("User id cannot be null", exception.getMessage());
    }

    @Test
    void testFindAllEmail() {
        List<User> users = initListUsers();
        users.forEach(user -> userDAO.create(user));

        List<String> expected = users.stream().map(User::getEmail).collect(Collectors.toList());
        List<String> actual = userDAO.findAllEmail();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    private List<User> initListUsers() {
        return List.of(new User("Alexey", "alexey@mail.ru", 33),
                new User("Alexander", "Alexander@mail.ru", 24),
                new User("Ivan", "Ivan@mail.ru", 20),
                new User("Georgi", "Georgi@mail.ru", 18),
                new User("Maks", "Maks@mail.ru", 10),
                new User("Valentin", "Valentin@mail.ru", 100));
    }
}