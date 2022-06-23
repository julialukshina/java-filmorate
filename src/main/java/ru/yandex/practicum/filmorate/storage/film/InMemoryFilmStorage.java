package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage { //реализация фильмохранилища
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override // возвращает лист со всеми фильмами
    public List<Film> getAllFilms() { // возвращает список имеюшщихся фильмов
        return new ArrayList<>(films.values());
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
        if (film.getId() <= 0) {
            throw new NotFoundException("Id должен быть положительным");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        log.info("Информация о фильме с id {} обновлена", film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Film film) { //удаляет данные по фильму
        if (film.getId() <= 0) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
        films.remove(film.getId());
        log.info("Информация о фильме с id {} удалена", film.getId());
    }

    @Override
    public Film getFilmById(Integer id) { //возвращает фильм по id
        if (id <= 0) {
            throw new NotFoundException("Id должен быть больше нуля");
        }
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
        log.info("Информация о фильме с id {} предоставлена", id);
        return films.get(id);
    }

    @Override //возвращает лист с id всех фильмов
    public List<Integer> getAllFilmsId() {
        return new ArrayList<>(films.keySet());
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        if (!getAllFilmsId().contains(filmId)) {
            throw new NotFoundException("Фильм с данным id не найден");
        }
        if (!userStorage.getAllUsersId().contains(userId)) {
            throw new NotFoundException("Пользователь с данным id не найден");
        }
        getFilmById(filmId).getLikes().add(userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        if (!getAllFilmsId().contains(filmId)) {
            throw new NotFoundException("Фильм с данным id не найден");
        }
        if (!userStorage.getAllUsersId().contains(userId)) {
            throw new NotFoundException("Пользователь с данным id не найден");
        }
        getFilmById(filmId).getLikes().remove(userId);
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
    }

    private int generateId() { //метод генерации id
        return id++;
    }

    public void clear() { //очищает хранилище
        films.clear();
        id = 1;
    }
}
