package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmHtmlDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.entity.Review;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface FilmService {

    List<FilmDto> getAllFilms();

    List<FilmHtmlDto> getAllFilmsHtml();

    FilmDto getFilmById(Long id);

    FilmDto createFilm(FilmCreateDto film);

    FilmDto updateFilm(Long id, FilmUpdateDto film);

    void deleteFilmById(Long id);

    List<FilmDto> getFilmsByGenre(Genre genre);

    List<FilmDto> getFilmsByRating(BigDecimal rating);

    void deleteReview(Long id);

    ReviewDto addReview(ReviewCreateDto review);

    HashMap<String, Review> getReviewsWithLogins(Long id);

    FilmDto addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<FilmDto> getTheMostLikedFilms(int count);
}
