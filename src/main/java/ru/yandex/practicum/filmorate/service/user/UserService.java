package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
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
        return storage.createUser(user);
    }

    public User updateUser(User user) { //обновляет данные пользователя
        return storage.updateUser(user);
    } //обновляет данные пользователя

    public void addFriend(Integer id1, Integer id2) throws ValidationException { //добавляет в друзья
        storage.addFriend(id1, id2);
    }

    public void deleteFriend(Integer id1, Integer id2) throws ValidationException { //удаляет из друзей
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
        return storage.getUserById(id);
    } //возвращает пользователя по id
}
