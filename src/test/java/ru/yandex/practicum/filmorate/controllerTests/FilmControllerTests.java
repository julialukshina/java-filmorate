package ru.yandex.practicum.filmorate.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    private Film film;
    private Film validFilm = new Film("Тимур и его команда", "Фильм для детей",
            LocalDate.of(1975, 04, 07), Duration.ofMinutes(124));
    ;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private FilmController filmController;

    @BeforeEach
    public void createFilmObject() {/*перед каждым тестом инициализируется поле фильм, эталонному фильму присваивается id,
    создается нужное окружение*/
        filmController.getAllFilms().clear();
        film = new Film("Тимур и его команда", "Фильм для детей",
                LocalDate.of(1975, 04, 07), Duration.ofMinutes(124));
        validFilm.setId(1);
        filmController.resetId();
    }

    @Test
    public void goodCreateAndUpdate() throws Exception { //тест на корректное создание и обновление объекта
        String body = objectMapper.writeValueAsString(film);
        goodCreate(body); //метод POST будет выполнен корректно
        film.setId(1);
        assertEquals(film, filmController.getAllFilms().get(1));
        film.setDescription("Фильм для детей и взрослых");
        body = objectMapper.writeValueAsString(film);
        goodUpdate(body);//метод PUT будет выполнен корректно
        assertEquals(film, filmController.getAllFilms().get(1));
    }

    @Test
    public void bedCreateAndUpdate() throws Exception { //тест валидации полей класса фильм
        String body = objectMapper.writeValueAsString(film);
        goodCreate(body);
        film.setId(1);
        assertEquals(1, filmController.getAllFilms().size());
        assertEquals(film, filmController.getAllFilms().get(1));
        film.setDuration(Duration.ofMinutes(0)); //некорректное значение для продолжительности
        body = objectMapper.writeValueAsString(film);
        bedCreate(body);
        bedUpdate(film);
        film.setDuration(Duration.ofMinutes(124)); // возвращаем объект в первоначальное состояние
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


    private void goodCreate(String body) throws Exception { //метод корректного создания объекта
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void bedCreate(String body) throws Exception { //метод некорректного создания объекта
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        assertEquals(1, filmController.getAllFilms().size());
        assertEquals(validFilm, filmController.getAllFilms().get(1));
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
        assertEquals(validFilm, filmController.getAllFilms().get(1));
    }

}
