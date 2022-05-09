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
//@RequestMapping("/users")
@Validated
@Slf4j
public class UserController extends Controller<String, User>{ //класс RestController
    private final Map<Integer, String> idEmail = new HashMap<>();
    String path = "users";

    @Override
    @GetMapping("/{path}")
    public Map<String, User> getAll() {
        return map;
    }
    @Override
    @PostMapping("/{path}")
    public User create(@Valid @RequestBody User user) {
        if (map.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с таким адресом электронной почты уже существует");
        }
        user.setId(generateId());
        map.put(user.getEmail(), user);
        idEmail.put(user.getId(), user.getEmail());
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @Override
    @PutMapping("/{path}")
    public User update (@Valid @RequestBody User user) { //обновляет данные пользователя
        if (user.getId() == 0) {
            throw new IllegalArgumentException("Для создания нового пользователя испольльзуйте POST-запрос");
        }
        if (idEmail.containsKey(user.getId()) && !(idEmail.get(user.getId()).equals(user.getEmail()))) {
            map.remove(idEmail.get(user.getId()));
        }
        map.put(user.getEmail(), user);
        idEmail.put(user.getId(), user.getEmail());
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }
}
