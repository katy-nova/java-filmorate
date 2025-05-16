package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.user.UserSimpleDto;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.MPA;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmDto {
    // не ставлю никаких аннотаций тк это ДТО нужно для передачи из таблицы на фронт
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Genre genre;
    private BigDecimal rating;
    private Integer duration;
    private MPA mpaRating;
    private Set<ReviewDto> reviews;
    private Set<UserSimpleDto> likedBy;
}
