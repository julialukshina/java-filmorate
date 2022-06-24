package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.xml.bind.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {//класс-сервис по запросам по пользователям
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    //возвращает список пользователей
    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    //создает нового пользователя
    public User createNewUser(User user) {
        if (storage.getAllUsersId().contains(user.getEmail())) {
            throw new UserAlreadyExistException("Пользователь с таким адресом электронной почты уже существует");
        }
        return storage.createUser(user);
    }

    public User updateUser(User user) { //обновляет данные пользователя
        if (user.getId() <= 0) {
            throw new NotFoundException("Id должен быть положительным");
        }
        return storage.updateUser(user);
    } //обновляет данные пользователя

    public void addFriend(Integer id1, Integer id2) throws ValidationException { //добавляет в друзья
        if (!storage.getAllUsersId().contains(id1) || !storage.getAllUsersId().contains(id2)) {
            throw new NotFoundException("Пользователь с такими id не существует");
        }
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        if (user1.getFriends().contains(id2) && !user2.getFriends().contains(id1)) {
            String message = String.format("Пользователь с id = %s уже добавлен в друзья пользователю с id = %s", id2, id1);
            throw new ru.yandex.practicum.filmorate.exceptions.ValidationException(message);
        }
        if (user1.getFriends().contains(id2) && user2.getFriends().contains(id1)) {
            String message = String.format("Пользователь с id = %s и пользователь с id = %s уже друг у друга в друзьях", id1, id2);
            throw new ru.yandex.practicum.filmorate.exceptions.ValidationException(message);
        }
        storage.addFriend(id1, id2);
    }

    public void deleteFriend(Integer id1, Integer id2) throws ValidationException { //удаляет из друзей
        if (!storage.getAllUsersId().contains(id1) || !storage.getAllUsersId().contains(id2)) {
            throw new NotFoundException("Пользователь с такими id не существует");
        }
        String sqlQuery;
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        if (!user1.getFriends().contains(id2) && !user2.getFriends().contains(id1)) {
            throw new ru.yandex.practicum.filmorate.exceptions.ValidationException("Пользователи с такими id не состоят в дружеских отношениях");
        }
        if (!user1.getFriends().contains(id2) && user2.getFriends().contains(id1)) {
            String message = String.format("Пользователь с id = %s не может изменять друзей пользователя с id = %s", id1, id2);
            throw new ru.yandex.practicum.filmorate.exceptions.ValidationException(message);
        }
        storage.deleteFriend(id1, id2);
    }

    public List<User> getFriends(Integer id) { //возвращает список друзей
        return storage.getUserById(id).getFriends().stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id1, Integer id2) { //возвращает список общих друзей
        if (id1 <= 0 || id2 <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными");
        }
        Set<Integer> commonFriendsId = new HashSet<>(storage.getUserById(id1).getFriends());
        commonFriendsId.retainAll(storage.getUserById(id2).getFriends());
        return commonFriendsId.stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }

    public User getUserById(Integer id) {
        if (!storage.getAllUsersId().contains(id)) {
            throw new NotFoundException("Пользователь с таким id не существует");
        }
        return storage.getUserById(id);
    } //возвращает пользователя по id
}
