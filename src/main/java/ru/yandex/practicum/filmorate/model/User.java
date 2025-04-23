package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotEmpty(message = "Это поле обязательно для заполнения")
    @Pattern(regexp = "^\\S*$", message = "Поле не должно содержать пробелы")
    @UniqueElements
    private String login;

    @NotEmpty(message = "Это поле обязательно для заполнения")
    @Email(message = "Неверный формат email")
    private String email;

    @Past
    private LocalDate birthday;

}
