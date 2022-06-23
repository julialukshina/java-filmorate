package ru.yandex.practicum.filmorate.controllerTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.model.enums.GENRES;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class GenreControllerTest {
    private final MockMvc mockMvc;
    private final GenreController genreController;

    @Autowired
    public GenreControllerTest(MockMvc mockMvc, GenreController genreController) {
        this.mockMvc = mockMvc;
        this.genreController = genreController;
    }

    @Test
    public void getAllRatings() throws Exception {
        this.mockMvc.perform(get("/genres"))
                .andExpect(status().isOk());
        assertEquals(6, genreController.getAll().size());
    }

    @Test
    public void getById() throws Exception {
        this.mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/genres/80"))
                .andExpect(status().isNotFound());
        assertEquals(GENRES.Комедия, genreController.getGenreById(1).getName());
    }
}
