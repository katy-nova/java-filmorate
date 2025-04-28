package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "films")
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Это поле обязательно для заполнения")
    private String name;

    @Size(max = 200)
    private String description;

    @Column(name = "release_date")
    @Past(message = "дата релиза должна быть в прошлом")
    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(precision = 3, scale = 1)
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private BigDecimal rating; // при использовании типа Double при запуске программа падает в exception

    @Positive(message = "длительность фильма не может быть отрицательной")
    private Integer duration;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany(mappedBy = "likedFilms")
    private Set<User> likedBy = new HashSet<>();
}