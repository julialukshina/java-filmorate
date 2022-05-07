package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Map<Integer, Film> getAllFilms() { // возвращает список имеюшщихся фильмов
        return films;
    }

    @PostMapping
    public Film createNewFilm(@Valid @RequestBody Film film) { //публикует информацию по новому фильму
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким id уже есть");
        }
        //     log.info(String.valueOf(user));
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Появилась информация о новом фильме: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) { //обновляет данные по фильму
        if (film.getId() == 0) {
            throw new IllegalArgumentException("Для публикации нового фильма испольльзуйте POST-запрос");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        log.info("Информация о фильме с id {} обновлена", film.getId());
        return film;
    }

    private int generateId() { //метод генерации id
        return id++;
    }
}
