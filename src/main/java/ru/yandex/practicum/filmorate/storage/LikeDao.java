package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LikeDao {
    private final JdbcTemplate jdbcTemplate;

    public LikeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void likeFilm(Integer filmId, Integer userId) { //пользователь ставит лайк

        String sqlQuery = "merge into film_likes (film_id, user_id) key (film_id, user_id)" +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = "delete from film_likes where film_id = ? and user_id=?";
        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);
    }

    public List<Integer> completionLikes(Integer filmId) {
        String sqlQuery = "select user_id from film_likes where film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
    }
}


