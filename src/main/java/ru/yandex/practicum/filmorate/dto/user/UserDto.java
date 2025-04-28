package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.film.FilmSimpleDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;

import java.time.LocalDate;
import java.util.Set;

//Здесь можно использовать аннотацию Дата?
@Data
public class UserDto {

    @NotBlank(message = "Это поле обязательно для заполнения")
    @Pattern(regexp = "^\\S*$", message = "Поле не должно содержать пробелы")
    private String login;

    @NotBlank(message = "Это поле обязательно для заполнения")
    @Email(message = "Неверный формат email")
    private String email;

    private String name;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    private Set<ReviewDto> reviews;

    private Set<UserSimpleDto> friends;

    private Set<FilmSimpleDto> likedFilms;
}
