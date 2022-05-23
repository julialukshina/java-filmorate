package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<String, User> users = new HashMap<>();
    private final Map<Integer, String> idEmail = new HashMap<>();
    private int id = 1;

    @Override //возвращает список пользователей
    public Map<String, User> getAllUsers() {
        return users;
    }

    @Override //создает нового пользователя
    public User createUser(User user) {
        if (users.containsKey(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с таким адресом электронной почты уже существует");
        }
        user.setId(generateId());
        users.put(user.getEmail(), user);
        idEmail.put(user.getId(), user.getEmail());
        log.info("Создан новый пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) { //обновляет данные пользователя
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

    @Override
    public void deleteUser(User user) { //обновляет данные пользователя
        if (user.getId() == 0) {
            throw new IllegalArgumentException("Пользователь с таким id не существует");
        }
        users.remove(user.getEmail());
        idEmail.remove(user.getId());
        log.info("Пользователь с id {} удален", user.getId());
    }

    @Override
    public User getUserById (Integer id){
        if (id <= 0) {
            throw new IllegalArgumentException("Id должен быть больше нуля");
        }
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("Пользователя с таким id не существует");
        }
        log.info("Информация о пользователе с id {} предоставлена", id);
        return users.get(id);
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
