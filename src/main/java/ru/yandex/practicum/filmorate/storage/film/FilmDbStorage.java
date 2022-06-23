package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.LikeDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingDao ratingDao;
    private final LikeDao likeDao;
    private final GenreDao genreDao;

    private final UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RatingDao ratingDao, LikeDao likeDao, GenreDao genreDao, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingDao = ratingDao;
        this.likeDao = likeDao;
        this.genreDao = genreDao;
        this.userStorage = userStorage;
    }

    @Override
    public Film createFilm(Film film) {
        if (getAllFilmsId().contains(film.getId())) {
            throw new ValidationException("Фильм с таким id уже есть");
        }
        String sqlQuery = "insert into films (name, description, release_Date, duration, rating_id) " +
                "values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        sqlQuery = "select film_id from films order by film_id desc limit 1";
        Integer filmId = jdbcTemplate.queryForObject(sqlQuery, Integer.class);
        if (film.getGenres() != null) {
            genreDao.saveGenres(film.getGenres(), filmId);
        }
        film.setId(filmId);
        log.info("Появилась информация о новом фильме: {}", film);
        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            throw new NotFoundException("Id должен быть положительным");
        }
        String sqlQuery = "update films set name=?, description =?, release_Date=?, duration=?, rating_id=? where film_id=?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            genreDao.updateGenres(film);
        }
        log.info("Информация о фильме с id {} обновлена", film.getId());
        Film film1 = getFilmById(film.getId());
        if (film.getGenres() != null && film1.getGenres() == null) {
            film1.setGenres(new ArrayList<>());
            return film1;
        }
        return film1;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public void deleteFilm(Film film) {
        if (!getAllFilmsId().contains(film.getId())) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        log.info("Информация о фильме с id {} удалена", film.getId());
    }

    @Override
    public Film getFilmById(Integer id) {
        if (id <= 0) {
            throw new NotFoundException("Id должен быть больше нуля");
        }
        if (!getAllFilmsId().contains(id)) {
            throw new NotFoundException("Фильма с таким id не существует");
        }
        log.info("Информация о фильме с id {} предоставлена", id);
        String sqlQuery = "select * from films where film_id =?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public List<Integer> getAllFilmsId() {
        String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs).getId());
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_Date").toLocalDate();
        Integer duration = rs.getInt("duration");
        Integer ratingId = rs.getInt("rating_id");
        Rating mpa = ratingDao.get(ratingId);
        Film film = new Film(id, name, description, releaseDate, duration, mpa);
        film.getLikes().addAll(likeDao.completionLikes(id));
        if (!genreDao.makeFilmGenres(id).isEmpty()) {
            film.setGenres(genreDao.makeFilmGenres(id));
        }
        return film;
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) { //пользователь ставит лайк
        if (!getAllFilmsId().contains(filmId)) {
            throw new NotFoundException("Фильм с данным id не найден");
        }
        if (!userStorage.getAllUsersId().contains(userId)) {
            throw new NotFoundException("Пользователь с данным id не найден");
        }
        likeDao.likeFilm(filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {//пользователь удаляет лайк
        if (!getAllFilmsId().contains(filmId)) {
            throw new NotFoundException("Фильм с данным id не найден");
        }
        if (!userStorage.getAllUsersId().contains(userId)) {
            throw new NotFoundException("Пользователь с данным id не найден");
        }
        likeDao.deleteLike(filmId, userId);
    }
}
