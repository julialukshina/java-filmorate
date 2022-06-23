package ru.yandex.practicum.filmorate.controllerTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.RatingController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class RatingControllerTest {
    private final MockMvc mockMvc;
    private final RatingController ratingController;

    @Autowired
    public RatingControllerTest(MockMvc mockMvc, RatingController ratingController) {
        this.mockMvc = mockMvc;
        this.ratingController = ratingController;
    }

    @Test
    public void getAllRatings() throws Exception {
        this.mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk());
        assertEquals(5, ratingController.getAll().size());
    }

    @Test
    public void getById() throws Exception {
        this.mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/mpa/80"))
                .andExpect(status().isNotFound());
        assertEquals("R", ratingController.getRatingById(4).getName());
    }
}
