package ru.duskhunter.aston.lesson2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@Getter
@Entity
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Setter
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Имя обязательно и должно содержать символы")
    @Size(max = 100, message = "Максимальная длина имени — 100 символов")
    String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email обязателен и должен содержать символы")
    @Size(max = 100, message = "Максимальная длина email — 100 символов")
    String email;

    @Setter
    @Column(name = "age", nullable = false)
    @Min(value = 1, message = "Возраст должен быть больше 0")
    @Max(value = 150, message = "Возраст должен быть меньше или равен 150")
    @NotNull(message = "Возраст должен быть заполнен")
    int age;

    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    }
}
