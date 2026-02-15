package ru.duskhunter.aston.lesson2.dao;

import jakarta.persistence.*;
import jakarta.validation.*;
import lombok.extern.log4j.Log4j2;
import ru.duskhunter.aston.lesson2.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class UserDAOImpl implements UserDAO {
    private final EntityManagerFactory emFactory;
    private final ValidatorFactory validatorFactory;

    public UserDAOImpl() {
        try {
            log.info("Loading persistence unit from: {}",
                    getClass().getResource("/META-INF/persistence.xml"));

            this.emFactory = Persistence
                    .createEntityManagerFactory("db-univer");

            this.validatorFactory = Validation.buildDefaultValidatorFactory();

            log.info("EntityManagerFactory created. Loaded entities: {}",
                    emFactory.getMetamodel().getEntities().size());

        } catch (Exception e) {
            log.error("Failed to create EntityManagerFactory", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() != null) {
            throw new IllegalArgumentException("New user has id");
        }

        validateUser(user);

        EntityManager entityManager = emFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
            log.info("User created successfully: {}", user);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error creating user: {}", user, e);
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            log.error("find user with null id");
            throw new IllegalArgumentException("id cannot be null");
        }
        EntityManager entityManager = emFactory.createEntityManager();
        try (entityManager) {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                log.info("Found user: {} with id: {}", user.getName(), id);
            } else {
                log.warn("User not found with id: {}", id);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("Error find user by id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager entityManager = emFactory.createEntityManager();
        try (entityManager) {
            TypedQuery<User> query = entityManager.createQuery("FROM User", User.class);
            List<User> users = query.getResultList();
            log.info("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Failed to find all users", e);
            return null;
        }
    }

    @Override
    public void update(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if(user.getId() == null){
            throw new IllegalArgumentException("User id cannot be null");
        }

        validateUser(user);

        long id = user.getId();

        EntityManager entityManager = emFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();
            User existingUser = entityManager.find(User.class, id);
            if (existingUser == null) {
                throw new IllegalArgumentException("User not found with id: " + id);
            }
            entityManager.merge(user);
            transaction.commit();
            log.info("user updated with id: {}", id);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error update user: {}, with id: {}", user, id, e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        EntityManager entityManager = emFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try (entityManager) {
            transaction.begin();
            User user = entityManager.find(User.class, id);

            if (user == null) {
                throw new IllegalArgumentException("User not found with id: " + id);
            }

            entityManager.remove(user);
            transaction.commit();
            log.info("User with id: {} has been remove", id);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Error delete user by id: {}", id);
            throw e;
        }
    }

    @Override
    public List<String> findAllEmail() {
        EntityManager entityManager = emFactory.createEntityManager();
        TypedQuery<String> query = entityManager.createQuery("select u.email from User u", String.class);
        return query.getResultList();
    }


    @Override
    public void close() {
        if (emFactory != null && emFactory.isOpen()) {
            emFactory.close();
            log.info("Entity manager factory is close");
        }
        if (validatorFactory != null) {
            validatorFactory.close();
            log.info("Validator factory is close");
        }
    }

    private void validateUser(User user) throws ConstraintViolationException {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            log.error("Validation failed: {}", errorMessage);
            throw new ConstraintViolationException(errorMessage, violations);
        }
    }
}
