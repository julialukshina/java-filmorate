package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User { //класс-модель для пользователей
    private int id;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "\\S*$")
    private String login;
    @NotNull
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Integer> friends;

    public User(String email, String login, String name, LocalDate birthday) { /* конструктор, позволяющий
        создать объект без id*/
        this.email = email;
        this.login = login;
        if (name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }

    public Set<Integer> getFriends() {
        return friends;
    }
}
