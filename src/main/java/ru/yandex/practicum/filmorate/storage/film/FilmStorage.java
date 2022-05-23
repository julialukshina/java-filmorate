package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Map<Integer, Film> getAllFilms();

    void deleteFilm(Film film);

    Film getFilmById(Integer id);
}
