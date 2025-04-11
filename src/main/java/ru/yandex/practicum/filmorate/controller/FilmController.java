package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/films")
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping(path = "/{id}")
    public Film getFilmById(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping(params = "genre")
    public List<Film> getFilmsByGenre(@RequestParam Genre genre) {
        return filmService.getFilmsByGenre(genre);
    }

    @GetMapping(params = "minRating")
    public List<Film> getFilmsByMinimumRating(@RequestParam BigDecimal minRating) {
        return filmService.getFilmsByRating(minRating);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(path = "/{id}")
    public Film updateFilm(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam String description,
                           @RequestParam Genre genre,
                           @Past @RequestParam LocalDate releaseDate,
                           @RequestParam int duration) {
        return filmService.updateFilm(id, name, description, genre, duration, releaseDate);
    }

    @PostMapping(path = "/review")
    public Review addReview(@RequestParam Review review) {
        return filmService.addReview(review);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilmById(id);
    }

    @DeleteMapping(path = "/review/{id}")
    public void deleteReview(@PathVariable Long id) {
        filmService.deleteReview(id);
    }
}
