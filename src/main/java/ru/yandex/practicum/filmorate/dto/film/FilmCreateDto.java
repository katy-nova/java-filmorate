package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;

@Data
public class FilmCreateDto {
    // отдельное ДТО для создания, чтобы поле рейтинг невозможно было задать вручную

    @NotBlank(message = "Это поле обязательно для заполнения")
    private String name;

    @Size(max = 200)
    private String description;

    @Past(message = "дата релиза должна быть в прошлом")
    private LocalDate releaseDate;

    private Genre genre;

    @Positive(message = "длительность фильма не может быть отрицательной")
    private int duration;
}
