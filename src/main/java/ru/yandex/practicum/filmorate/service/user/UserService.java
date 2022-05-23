package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    //возвращает список пользователей
    public Map<String, User> getAllUsers() {
        return storage.getAllUsers();
    }

    //создает нового пользователя
    public User createNewUser(User user) {
        return storage.createUser(user);
    }

    public User updateUser(User user) { //обновляет данные пользователя
        return storage.updateUser(user);
    }

    public void addFriend(Integer id1, Integer id2) {
        storage.getUserById(id1).getFriends().add(id2);
        storage.getUserById(id2).getFriends().add(id1);
        log.info("Пользователи с id {} и {} добавлены в друзья", id1, id2);
    }

    public void deleteFriend (Integer id1, Integer id2){
        storage.getUserById(id1).getFriends().remove(id2);
        storage.getUserById(id2).getFriends().remove(id1);
        log.info("Пользователи с id {} и {} удалены из друзей", id1, id2);
    }

    public List<User> getFriends (Integer id){
       return storage.getUserById(id).getFriends().stream()
               .map(storage::getUserById)
               .collect(Collectors.toList());
    }

    public List <User> getCommonFriends (Integer id1, Integer id2){
        Set<Integer> commonFriendsId = new HashSet<>(storage.getUserById(id1).getFriends());
        commonFriendsId.retainAll(storage.getUserById(id2).getFriends());
        return commonFriendsId.stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }
}
