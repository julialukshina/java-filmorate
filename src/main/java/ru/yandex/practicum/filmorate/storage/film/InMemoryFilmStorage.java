package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

   @Override
    public Map<Integer, Film> getAllFilms() { // возвращает список имеюшщихся фильмов
        return films;
    }

    @Override
    public Film createFilm(Film film) { //публикует информацию по новому фильму
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким id уже есть");
        }
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Появилась информация о новом фильме: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) { //обновляет данные по фильму
        if (film.getId() == 0) {
            throw new IllegalArgumentException("Для публикации нового фильма испольльзуйте POST-запрос");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        log.info("Информация о фильме с id {} обновлена", film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Film film) { //обновляет данные по фильму
        if (film.getId() == 0) {
            throw new IllegalArgumentException("Фильм с таким id не найден");
        }
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
        }
        log.info("Информация о фильме с id {} удалена", film.getId());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id должен быть больше нуля");
        }
        if (!films.containsKey(id)) {
            throw new IllegalArgumentException("Фильма с таким id не существует");
        }
        log.info("Информация о фильме с id {} предоставлена", id);
        return films.get(id);
    }
    private int generateId() { //метод генерации id
        return id++;
    }

    public void resetId() {
        id = 1;
    }
}
