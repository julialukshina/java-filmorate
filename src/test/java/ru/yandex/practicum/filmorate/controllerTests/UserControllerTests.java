package ru.yandex.practicum.filmorate.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    private User user;
    private User validUser = new User("sashaivanova@yandex.ru", "sasha", "Alexandra",
            LocalDate.of(1983, 02, 02));
    private String body;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController userController;

    @Autowired
    private InMemoryUserStorage inMemoryUserStorage;

    @BeforeEach /*перед каждым тестом инициализируется поле пользователь, эталонному пользователю присваивается id,
    создается нужное окружение*/
    public void createUserObject() throws Exception {
        user = new User("sashaivanova@yandex.ru", "sasha", "Alexandra",
                LocalDate.of(1983, 02, 02));
        inMemoryUserStorage.clear();
        validUser.setId(1);
        body = objectMapper.writeValueAsString(user);
        goodCreate(body);
        user.setId(1);
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
        final String email = user.getEmail();
        user.setEmail(""); //некорректное значение для email
        body = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getAllUsers().size());
        assertFalse(inMemoryUserStorage.getAllUsersEmails().contains(user.getEmail()));
        assertEquals(validUser, userController.getAllUsers().get(0));
        this.mockMvc.perform(put("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, userController.getAllUsers().size());
        assertFalse(inMemoryUserStorage.getAllUsersEmails().contains(user.getEmail()));
        assertEquals(validUser, userController.getAllUsers().get(0));
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
