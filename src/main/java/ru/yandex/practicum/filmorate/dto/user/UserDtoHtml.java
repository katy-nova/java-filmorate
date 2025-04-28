package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDtoHtml {

    private Long id;
    private String name;
    private String email;
    private String login;
    private LocalDate birthday;
}
