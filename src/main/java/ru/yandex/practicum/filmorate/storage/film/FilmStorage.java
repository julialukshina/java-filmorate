package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage { //интерфейс для film-хранилища
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    void deleteFilm(Film film);

    Film getFilmById(Integer id);

    List<Integer> getAllFilmsId();

    void likeFilm(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);
}
