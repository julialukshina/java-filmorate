package ru.yandex.practicum.filmorate.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class UserControllerTests {
    private User user;
    private final User validUser = new User(1, "sashaivanova@yandex.ru", "sasha", "Alexandra",
            LocalDate.of(1983, 02, 02));
    private String body;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    private final UserController userController;

    private final UserStorage userDbStorage;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserControllerTests(ObjectMapper objectMapper, MockMvc mockMvc, UserController userController,
                               UserStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.userController = userController;
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }


    @BeforeEach //перед каждым тестом инициализируется поле пользователь, создается нужное окружение
    public void createUserObject() throws Exception {
        String sqlQuery = "delete from film_genre"; //обнуляем базу
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "delete from film_likes";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "delete from FRIENDSHIP";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "delete from films";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "delete from users";
        jdbcTemplate.update(sqlQuery);

        sqlQuery = "ALTER TABLE film_genre ALTER COLUMN id RESTART WITH 1"; //скидываем счетчики
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE film_likes ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE friendship ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);
        sqlQuery = "ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1";
        jdbcTemplate.update(sqlQuery);

        user = new User(1, "sashaivanova@yandex.ru", "sasha", "Alexandra",
                LocalDate.of(1983, 02, 02));
        body = objectMapper.writeValueAsString(user);
        goodCreate(body);
        assertEquals(1, userController.getAllUsers().size()); //проверяем корректность создания объекта
        assertEquals(user, userController.getAllUsers().get(0));
    }

    @Test
    public void goodUpdate() throws Exception { //тест на корректное создание и обновление объекта
        user.setName("Александра");
        body = objectMapper.writeValueAsString(user);
        goodUpdate(body);//метод PUT будет выполнен корректно
        assertEquals(user, userController.getAllUsers().get(0));
    }

    @Test
    public void bedCreateAndUpdateEmail() throws Exception { //тест валидации  email
        user.setEmail(""); //некорректное значение для email
        body = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getAllUsers().size());
        assertFalse(userDbStorage.getAllUsersEmails().contains(user.getEmail()));
        assertEquals(validUser, userController.getAllUsers().get(0));
        this.mockMvc.perform(put("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getAllUsers().size());
        assertFalse(userDbStorage.getAllUsersEmails().contains(user.getEmail()));
        assertEquals(validUser, userController.getAllUsers().get(0));
    }

    @Test
    public void createAndDeleteFriendship() throws Exception { //тест на добавление и удаление друзей
        User user2 = new User(2, "ivanova@yandex.ru", "Ivanova", "Masha",
                LocalDate.of(1988, 01, 12));
        body = objectMapper.writeValueAsString(user2);
        goodCreate(body);
        assertEquals(2, userController.getAllUsers().size()); //проверяем корректность создания объекта
        assertEquals(user2, userController.getAllUsers().get(1));
        this.mockMvc.perform(put("/users/1/friends/2")) //корректное добавление друга
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/users/1/friends/2")) //некорректное добавление друга
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getUserById(1).getFriends().size());
        this.mockMvc.perform(delete("/users/1/friends/2")) //корректное удаление друга
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/users/1/friends/2")) //некорректное удаление друга
                .andExpect(status().isBadRequest());
        assertEquals(0, userController.getUserById(1).getFriends().size());
    }

    @Test
    public void bedCreateAndUpdateLogin() throws Exception { //тест валидации логина
        user.setLogin("sa sha"); //некорректное значение для логина
        body = objectMapper.writeValueAsString(user);
        bedCreate(body);
        bedUpdate(user);
    }

    @Test
    public void bedCreateAndUpdateName() throws Exception { //тест валидации имени
        user.setEmail("yandex@yandex.ru");
        user.setName("");
        body = objectMapper.writeValueAsString(user);
        goodCreate(body);
        assertEquals(userController.getAllUsers().get(1).getName(), userController.getAllUsers().
                get(1).getLogin());
    }

    @Test
    public void bedCreateAndUpdateBirthday() throws Exception { //тест валидации даты рождения
        user.setBirthday(LocalDate.of(2122, 06, 01)); //некорректная дата рождения
        body = objectMapper.writeValueAsString(user);
        bedCreate(body);
        bedUpdate(user);
    }

    private void goodCreate(String body) throws Exception { //метод корректного создания объекта
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void bedCreate(String body) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getAllUsers().size());
        assertEquals(validUser, userController.getAllUsers().get(0));
    }

    private void goodUpdate(String body) throws Exception { //метод корректного обновления объекта
        this.mockMvc.perform(put("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void bedUpdate(User user) throws Exception { //метод некорректного обновления объекта
        user.setId(1);
        body = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(put("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getAllUsers().size());
        assertEquals(validUser, userController.getAllUsers().get(0));
    }
}
