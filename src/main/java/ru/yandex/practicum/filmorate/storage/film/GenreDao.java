package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.GENRES;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre get(Integer genreId) {
        if (genreId < 0 || genreId > 6) {
            throw new NotFoundException("Жанр с таким id не существует");
        }
        String sqlQuery = "select * from genres where genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeGenre(rs), genreId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");
        Genre genre = new Genre(id);
        genre.setName(GENRES.valueOf(name));
        return genre;
    }

    public List<Genre> makeFilmGenres(Integer filmId) {
        String sqlQuery = "select genre_id from film_genre where film_id=?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId).stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    public List<Genre> getAll() {
        String sqlQuery = "select genre_id from genres";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class).stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    public void saveGenres(List<Genre> genres, int filmId) {
        String sqlQuery = "merge into film_genre (film_id, genre_id) key(film_id, genre_id) " +
                "values (?, ?)";
        genres.forEach(s -> jdbcTemplate.update(sqlQuery, filmId, s.getId()));
    }

    public void updateGenres(Film film) {
        String sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        if (!film.getGenres().isEmpty()) {
            saveGenres(film.getGenres(), film.getId());
        }
    }
}
