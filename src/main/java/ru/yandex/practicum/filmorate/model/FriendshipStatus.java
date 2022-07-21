package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.model.enums.FRIENDSHIP_STATUSES;

public class FriendshipStatus {
    private Integer id;
    private final FRIENDSHIP_STATUSES name;

    public FriendshipStatus(FRIENDSHIP_STATUSES name) {
        this.name = name;
    }

}
