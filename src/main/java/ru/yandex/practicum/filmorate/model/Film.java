package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film { //класс-модель для фильмов
    private int id;
    @NotNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    public Film(String name, String description, LocalDate releaseDate, Duration duration) { /* конструктор, позволяющий
     создать объект без id*/
        validation(releaseDate, duration);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    private void validation(LocalDate releaseDate, Duration duration) { //метод для валидации даты релиза и продолжительности
        LocalDate date = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(date)) {
            throw new ValidationException("Дата релиза указана раньше 28 января 1895 года");
        }
        if (duration.isNegative() || duration.isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
