package ru.yandex.practicum.filmorate.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {

    @Nullable
    @Pattern(regexp = "^\\S*$", message = "Поле не должно содержать пробелы")
    private String login;

    @Nullable
    @Email(message = "Неверный формат email")
    private String email;

    @Nullable
    private String name;

    @Nullable
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
}
