package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User { //класс-модель для пользователей
    private int id;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String login;
    @NotNull
    private String name;
    @Past
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) { /* конструктор, позволяющий
        создать объект без id*/
        validation(login);
        this.email = email;
        this.login = login;
        this.name = name;
        if (name.isBlank()) {
            name = login;
        }
        this.birthday = birthday;
    }

    private void validation(String login) { //метод для валидации логина
        if (login.contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелов");
        }
    }
}
