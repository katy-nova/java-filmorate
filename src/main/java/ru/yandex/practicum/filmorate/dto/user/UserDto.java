package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.film.FilmSimpleDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.model.Role;

import java.time.LocalDate;
import java.util.Set;

//Здесь можно использовать аннотацию Дата?
@Data
public class UserDto {

    private String login;

    private String email;

    private String name;

    private LocalDate birthday;

    private Set<ReviewDto> reviews;

    private Set<UserSimpleDto> friends;

    private Set<FilmSimpleDto> likedFilms;

    private Set<Role> roles;
}
