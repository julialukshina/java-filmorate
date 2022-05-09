package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController extends Controller<Integer, Film>{

   @Override
    @GetMapping("/{path}")
    public Map<Integer, Film> getAll() {
        return map;
    }
    @Override
    @PostMapping
    public Film create(@Valid @RequestBody Film film) { //публикует информацию по новому фильму
        if (map.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким id уже есть");
        }
        film.setId(generateId());
        map.put(film.getId(), film);
        log.info("Появилась информация о новом фильме: {}", film);
        return film;
    }

    @Override
    @PutMapping
    public Film update(@Valid @RequestBody Film film) { //обновляет данные по фильму
        if (film.getId() == 0) {
            throw new IllegalArgumentException("Для публикации нового фильма испольльзуйте POST-запрос");
        }
        if (map.containsKey(film.getId())) {
            map.put(film.getId(), film);
        }
        log.info("Информация о фильме с id {} обновлена", film.getId());
        return film;
    }
}
