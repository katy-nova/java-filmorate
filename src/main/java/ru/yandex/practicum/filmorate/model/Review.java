package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Это поле не может быть пустым")
    private Long userId;

    @NotNull
    private Long filmId;

    @Size(min = 1, max = 200)
    private String text;

    @Column(precision = 3, scale = 1)
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal rating;
}
