package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString

public class Film { //класс-модель для фильмов
    private int id;
    @NotNull
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private LocalDate releaseDate;
    @NotNull
    @Min(1)
    private Integer duration;
    private Set<Integer> likes = new HashSet<>();

    public Set<Integer> getLikes() {
        return likes;
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration) { /* конструктор, позволяющий
     создать объект без id*/
        validation(releaseDate);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    private void validation(LocalDate releaseDate) { //метод для валидации даты релиза и продолжительности
        LocalDate date = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(date)) {
            throw new ValidationException("Дата релиза указана раньше 28 января 1895 года");
        }
    }
}
