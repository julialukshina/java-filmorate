package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.RatingDao;

import java.util.List;

@Service
@Slf4j
public class RatingService {
    private final RatingDao ratingDao;

    public RatingService(RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    public List<Rating> getAllRatings() {
        return ratingDao.getAll();
    }

    public Rating getRatingById(Integer id) {
        return ratingDao.get(id);
    }
}

