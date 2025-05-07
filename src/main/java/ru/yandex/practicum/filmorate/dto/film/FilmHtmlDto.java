package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FilmHtmlDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Genre genre;
    private BigDecimal rating;
    private int duration;

}
