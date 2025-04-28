package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmServiceDB;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/films")
@AllArgsConstructor
public class FilmController {

    private final FilmServiceDB filmService;

    @GetMapping
    public List<FilmDto> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping(path = "/{id}")
    public FilmDto getFilmById(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping(params = "genre")
    public List<FilmDto> getFilmsByGenre(@RequestParam Genre genre) {
        return filmService.getFilmsByGenre(genre);
    }

    @GetMapping(params = "minRating")
    public List<FilmDto> getFilmsByMinimumRating(@RequestParam BigDecimal minRating) {
        return filmService.getFilmsByRating(minRating);
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmCreateDto film) {
        return filmService.createFilm(film);
    }

    @PutMapping(path = "/{id}")
    public FilmDto updateFilm(@PathVariable Long id, @Valid @RequestBody FilmUpdateDto film) {
        return filmService.updateFilm(id, film);
    }

    @PostMapping(path = "/review")
    public ReviewDto addReview(@RequestParam ReviewCreateDto review) {
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

    @PutMapping(path = "/{userId}/like/{filmId}")
    public FilmDto addLike(@PathVariable Long userId, @PathVariable Long filmId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping(path = "/{userId}/like/{filmId}")
    public void deleteLike(@PathVariable Long userId, @PathVariable Long filmId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping(path = "/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTheMostLikedFilms(count);
    }

}
