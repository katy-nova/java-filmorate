package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Role;

import java.time.LocalDate;
import java.util.Set;

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

    @NotEmpty(message = "Роль должна быть указана")
    private Set<Role> roles;
}
