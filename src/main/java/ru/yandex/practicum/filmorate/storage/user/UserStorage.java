package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User createUser(User user);
    User updateUser (User user);
    List<User> getAllUsers();
    void deleteUser (User user);

    User getUserById (Integer id);

    List<Integer> getAllUsersId();
}
