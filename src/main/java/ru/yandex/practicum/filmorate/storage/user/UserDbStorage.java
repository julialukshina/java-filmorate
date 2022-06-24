package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "insert into users (email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        sqlQuery = "select user_id from users order by user_id desc limit 1";
        Integer userId = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
        user.setId(userId);
        log.info("Создан новый пользователь: {}", user);
        return getUserById(userId);
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set email=?, login =?, name=?, birthday=? where user_id=?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Пользователь с id {} обновлен", user.getId());
        return getUserById(user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User user = new User(id, email, login, name, birthday);
        user.getFriends().addAll(completionFriends(id));
        return user;
    }

    @Override
    public void deleteUser(User user) { //метод не используется, написан, так как логически требуется, пока осталю тут
        if (!getAllUsersId().contains(user.getId())) {
            throw new NotFoundException("Пользователь с таким id не существует");
        }
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
        log.info("Пользователь с id {} удален", user.getId());
    }

    @Override
    public User getUserById(Integer id) {
        log.info("Информация о пользователе с id {} предоставлена", id);
        String sqlQuery = "select * from users where user_id =?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public List<Integer> getAllUsersId() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs).getId());
    }

    @Override
    public List<String> getAllUsersEmails() {
        return getAllUsers().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(Integer id1, Integer id2) throws ValidationException {
        String sqlQuery;
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        if (!user1.getFriends().contains(id2) && !user2.getFriends().contains(id1)) {
            sqlQuery = "insert into FRIENDSHIP (user_id, friend_id, status_id) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id1,
                    id2,
                    2);
            log.info("Пользователь с id {} добавил в друзья пользователя с id {}.Дружба не подтверждена", id1, id2);
        }
        if (!user1.getFriends().contains(id2) && user2.getFriends().contains(id1)) {
            sqlQuery = "insert into FRIENDSHIP (user_id, friend_id, status_id) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlQuery,
                    id1,
                    id2,
                    1);
            sqlQuery = "update FRIENDSHIP set STATUS_ID=? where user_id=? AND FRIEND_ID=?";
            jdbcTemplate.update(sqlQuery,
                    1,
                    id2,
                    id1);
            log.info("Пользователь с id {} и пользователь с id {} друг у друга в друзьях. Дружба подтверждена", id1, id2);
        }
    }

    @Override
    public void deleteFriend(Integer id1, Integer id2) throws ValidationException {
        String sqlQuery;
        User user1 = getUserById(id1);
        User user2 = getUserById(id2);
        if (user1.getFriends().contains(id2) && user2.getFriends().contains(id1)) {
            sqlQuery = "delete from friendship where user_id = ? and FRIEND_ID=?";
            jdbcTemplate.update(sqlQuery, id1, id2);
            sqlQuery = "delete from friendship where user_id = ? and FRIEND_ID=?";
            jdbcTemplate.update(sqlQuery, id2, id1);
            log.info("Пользователи с id {} и {} удалены друг у друга из друзей", id1, id2);
        }
        if (user1.getFriends().contains(id2) && !user2.getFriends().contains(id1)) {
            sqlQuery = "delete from friendship where user_id = ? and FRIEND_ID=?";
            jdbcTemplate.update(sqlQuery, id1, id2);
        }
    }

    public List<Integer> completionFriends(Integer userId) {
        String sqlQuery = "select friend_id from FRIENDSHIP where USER_ID = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId);
    }
}
