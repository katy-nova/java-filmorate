package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCreateDto {
    @NotBlank(message = "Это поле обязательно для заполнения")
    @Pattern(regexp = "^\\S*$", message = "Поле не должно содержать пробелы")
    private String login;

    @NotBlank(message = "Это поле обязательно для заполнения")
    @Email(message = "Неверный формат email")
    private String email;

    private String name;

    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    @NotBlank
    private String password;

}
