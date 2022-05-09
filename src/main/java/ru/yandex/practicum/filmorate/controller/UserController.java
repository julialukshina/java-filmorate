package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController { //класс RestController
    private final Map<String, User> users = new HashMap<>();
    private final Map<Integer, String> idEmail = new HashMap<>();
    private int id = 1;

    @GetMapping //возвращает список пользователей
    public Map<String, User> getAllUsers() {
        return users;
    }

    @PostMapping //создает нового пользователя
    public User createNewUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с таким адресом электронной почты уже существует");
        }
        user.setId(generateId());
        users.put(user.getEmail(), user);
        idEmail.put(user.getId(), user.getEmail());
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) { //обновляет данные пользователя
        if (user.getId() == 0) {
            throw new IllegalArgumentException("Для создания нового пользователя испольльзуйте POST-запрос");
        }
        if (idEmail.containsKey(user.getId()) && !(idEmail.get(user.getId()).equals(user.getEmail()))) {
            users.remove(idEmail.get(user.getId()));
        }
        users.put(user.getEmail(), user);
        idEmail.put(user.getId(), user.getEmail());
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }

    private int generateId() {//метод генерации id
        return id++;
    }

    public void resetController() {
        id = 1;
        users.clear();
        idEmail.clear();
    }
}