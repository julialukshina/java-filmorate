package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() { // возвращает список имеюшщихся фильмов
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film film) { //публикует информацию по новому фильму
        return filmService.createNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { //обновляет данные по фильму
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Integer id, @PathVariable Integer userId) { //пользователь ставит лайк
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) { //пользователь удаляет лайк
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getBestFilm(@RequestParam(defaultValue = "10") Integer count) { //возвращаем лучшие фильмы
        return filmService.getBestFilm(count);
    }

    @GetMapping ("/{id}")
    public Film getFilmById(@PathVariable Integer id){
        return filmService.getFilmById(id);
    }
}