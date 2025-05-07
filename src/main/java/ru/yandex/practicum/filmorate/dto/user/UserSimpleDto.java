package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

@Data
public class UserSimpleDto { // для отображения списка друзей/лайков

    private String name;
    private String login;
}
