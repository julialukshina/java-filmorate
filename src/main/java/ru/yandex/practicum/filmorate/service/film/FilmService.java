package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService { //класс-сервис по запросам по фильмам
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() { // возвращает список имеюшщихся фильмов
        return filmStorage.getAllFilms();
    } //возвращаем лист со всеми фильмами

    public Film createNewFilm(Film film) { //публикует информацию по новому фильму
        if (filmStorage.getAllFilmsId().contains(film.getId())) {
            throw new ValidationException("Фильм с таким id уже есть");
        }

        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) { //обновляет данные по фильму
        if (film.getId() <= 0) {
            throw new NotFoundException("Id должен быть положительным");
        }
        return filmStorage.updateFilm(film);
    }

    public void likeFilm(Integer id, Integer userId) { //пользователь ставит лайк
        if (!filmStorage.getAllFilmsId().contains(id)) {
            throw new NotFoundException("Фильм с данным id не найден");
        }
        if (!userStorage.getAllUsersId().contains(userId)) {
            throw new NotFoundException("Пользователь с данным id не найден");
        }
        filmStorage.likeFilm(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) { //пользователь удаляет лайк
        if (!filmStorage.getAllFilmsId().contains(id)) {
            throw new NotFoundException("Фильм с данным id не найден");
        }
        if (!userStorage.getAllUsersId().contains(userId)) {
            throw new NotFoundException("Пользователь с данным id не найден");
        }
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getBestFilm(Integer count) { //возвращаем лучшие фильмы
        if (count < 0) {
            throw new ValidationException("Если значение count определяется, то оно должно быть положительным.");
        }
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Integer id) { //возвращаем фильм по id
        if (id <= 0) {
            throw new NotFoundException("Id должен быть больше нуля");
        }
        if (!filmStorage.getAllFilmsId().contains(id)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
        return filmStorage.getFilmById(id);
    }
}
