package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Genre;

@Data
public class FilmSimpleDto {
    private String name;
    private Genre genre;
}
