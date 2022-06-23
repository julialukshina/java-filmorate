package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface UserStorage { //интерфейс для user-хранилища
    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    void deleteUser(User user);

    User getUserById(Integer id);

    List<Integer> getAllUsersId();

    void addFriend(Integer id1, Integer id2) throws ValidationException;

    void deleteFriend(Integer id1, Integer id2) throws ValidationException;

    List<String> getAllUsersEmails();
}
