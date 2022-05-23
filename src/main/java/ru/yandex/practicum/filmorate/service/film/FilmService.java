package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Map<Integer, Film> getAllFilms() { // возвращает список имеюшщихся фильмов
        return filmStorage.getAllFilms();
    }

    public Film createNewFilm(Film film) { //публикует информацию по новому фильму
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) { //обновляет данные по фильму
        return filmStorage.updateFilm(film);
    }

    public void likeFilm(Integer id, Integer userId) {
        if (userStorage.getAllUsers().containsKey(userId)) {
            filmStorage.getFilmById(id).getLikes().add(userId);
        }
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, id);
    }

    public void deleteLike(Integer id, Integer userId) {
        if (userStorage.getAllUsers().containsKey(userId)) {
            filmStorage.getFilmById(id).getLikes().remove(userId);
        }
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, id);
    }

    public List<Film> getBestFilm(Integer count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Если значение count определяется, то оно должно быть положительным.");
        }
        return filmStorage.getAllFilms().values().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}