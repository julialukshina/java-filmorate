package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Rating {
    private Integer id;
    private String name;

    @JsonCreator
    public Rating(Integer id) {
        this.id = id;
    }


    public Rating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}

