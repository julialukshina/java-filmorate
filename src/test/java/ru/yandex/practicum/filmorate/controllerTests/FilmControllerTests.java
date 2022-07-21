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
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class FilmControllerTest {
    private Film film;
    private final Rating validMpa = new Rating(1, "G");
    private final Film validFilm = new Film(1, "Тимур и его команда", "Фильм для детей",
            LocalDate.of(1975, 04, 07), 124, validMpa);

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    private final FilmController filmController;
    private final UserStorage userDbStorage;
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmControllerTest(ObjectMapper objectMapper, MockMvc mockMvc, FilmController filmController,
                              UserStorage userDbStorage, FilmStorage filmStorage, JdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.filmController = filmController;
        this.userDbStorage = userDbStorage;
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    public void createFilmObject() {//перед каждым тестом инициализируется поле фильм, создается нужное окружение
        Rating mpa = new Rating(1, "G");
        film = new Film(1, "Тимур и его команда", "Фильм для детей",
                LocalDate.of(1975, 04, 07), 124, mpa);
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
    }

    @Test
    public void goodCreateAndUpdate() throws Exception { //тест на корректное создание и обновление объекта
        String body = objectMapper.writeValueAsString(film);
        goodCreate(body); //метод POST будет выполнен корректно
        assertEquals(film, filmController.getAllFilms().get(0));
        film.setDescription("Фильм для детей и взрослых");
        body = objectMapper.writeValueAsString(film);
        goodUpdate(body);//метод PUT будет выполнен корректно
        assertEquals(film, filmController.getAllFilms().get(0));
    }

    @Test
    public void bedCreateAndUpdate() throws Exception { //тест валидации полей класса фильм
        String body = objectMapper.writeValueAsString(film);
        goodCreate(body);
        film.setId(1);
        assertEquals(1, filmController.getAllFilms().size());
        assertEquals(film, filmController.getAllFilms().get(0));
        film.setDuration(0); //некорректное значение для продолжительности
        body = objectMapper.writeValueAsString(film);
        bedCreate(body);
        bedUpdate(film);
        film.setDuration(124); // возвращаем объект в первоначальное состояние
        film.setId(0);
        //некорректное описание
        String bigDescription = new String(new char[201]).replace('\0', 'a');
        film.setDescription(bigDescription);
        body = objectMapper.writeValueAsString(film);
        bedCreate(body);
        bedUpdate(film);
        film.setDescription("Фильм для детей");
        film.setId(0);
        film.setReleaseDate(LocalDate.of(1895, 1, 27)); //некорректная дата релиза
        body = objectMapper.writeValueAsString(film);
        bedCreate(body);
        bedUpdate(film);
    }

    @Test
    public void filmLikes() throws Exception {
        String body = objectMapper.writeValueAsString(film);
        goodCreate(body);
        User user = new User(1, "sashaivanova@yandex.ru", "sasha", "Alexandra",
                LocalDate.of(1983, 02, 02));
        body = objectMapper.writeValueAsString(user);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(put("/films/1/like/28"))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(put("/films/100/like/1"))
                .andExpect(status().isNotFound());
        assertEquals(1, filmController.getFilmById(1).getLikes().size());
        this.mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/films/1/like/28"))
                .andExpect(status().isNotFound());
        this.mockMvc.perform(delete("/films/100/like/1"))
                .andExpect(status().isNotFound());
        assertEquals(0, filmController.getFilmById(1).getLikes().size());
    }

    private void goodCreate(String body) throws Exception { //метод корректного создания объекта
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void bedCreate(String body) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, filmController.getAllFilms().size());
        assertEquals(validFilm, filmController.getAllFilms().get(0));
    }

    private void goodUpdate(String body) throws Exception { //метод некорректного обновления объекта
        this.mockMvc.perform(put("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void bedUpdate(Film film) throws Exception { //метод некорректного обновления объекта
        film.setId(1);
        String body = objectMapper.writeValueAsString(film);
        this.mockMvc.perform(put("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, filmController.getAllFilms().size());
        assertEquals(validFilm, filmController.getAllFilms().get(0));
    }

}
