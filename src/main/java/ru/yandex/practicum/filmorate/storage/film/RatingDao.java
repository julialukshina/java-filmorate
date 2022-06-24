package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RatingDao {
    private final JdbcTemplate jdbcTemplate;

    public RatingDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Rating get(Integer ratingId) {
        String sqlQuery = "select * from rating where rating_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (rs, rowNum) -> makeRating(rs), ratingId);
    }

    private Rating makeRating(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("rating_id");
        String name = rs.getString("name");
        Rating rating = new Rating(id);
        rating.setName(name);
        return rating;
    }

    public List<Rating> getAll() {
        String sqlQuery = "select rating_id from rating";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class).stream()
                .map(this::get)
                .collect(Collectors.toList());
    }
}
