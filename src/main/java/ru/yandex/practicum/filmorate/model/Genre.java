package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.model.enums.GENRES;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Genre {
    private Integer id;
    private GENRES name;

    @JsonCreator
    public Genre(Integer id) {
        this.id = id;
    }

    public Genre(GENRES name) {
        this.name = name;
    }

}
