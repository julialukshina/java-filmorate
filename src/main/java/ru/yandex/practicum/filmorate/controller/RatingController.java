package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.film.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping //возвращаем лист со всеми рейтингами
    public List<Rating> getAll() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{id}")//возвращаем рейтинг по id
    public Rating getRatingById(@PathVariable int id) {
        return ratingService.getRatingById(id);
    }
}

