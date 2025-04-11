package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Entity
@Table(name = "films")
@NoArgsConstructor
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Это поле обязательно для заполнения")
    private String name;

    @Size(max = 200)
    private String description;

    @Column(name = "release_date")
    @Past
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(precision = 3, scale = 1)
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal rating; // при использовании типа Double при запуске программа падает в exception

    @Positive
    private int duration;

}
