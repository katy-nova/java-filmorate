package ru.yandex.practicum.filmorate.dto.film;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.MPA;

import java.time.LocalDate;

@Data
public class FilmUpdateDto {
    @Nullable
    private String name;

    @Nullable
    @Size(max = 200)
    private String description;

    @Nullable
    @Past
    private LocalDate releaseDate;

    @Nullable
    private Genre genre;

    @Nullable
    private MPA mpaRating;

    @Positive
    @Nullable
    private Integer duration;

}

