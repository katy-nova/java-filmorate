package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDtoHtml;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(path = "/api/films")
@AllArgsConstructor
public class FilmWebController {

    private final FilmService filmService;

    @GetMapping
    public String getFilms(Model model) {
        List<FilmDtoHtml> films = filmService.getAllFilmsHtml();
        model.addAttribute("films", films);
        return "films/list";
    }

    @GetMapping(path = "/{id}")
    public String getFilm(@PathVariable Long id, Model model) {
        FilmDto film = filmService.getFilmById(id);
        HashMap<String, Review> reviews = filmService.getReviewsWithLogins(id);
        model.addAttribute("reviews", reviews);
        model.addAttribute("numberOfReviews", reviews.size());
        model.addAttribute("film", film);
        model.addAttribute("filmId", id);
        return "films/filmInfo";
    }

    @GetMapping(path = "/new")
    public String newFilm(Model model) {
        model.addAttribute("film", new Film());
        return "films/new";
    }

    @PostMapping(path = "/new")
    public String saveFilm(@Valid @ModelAttribute("film") FilmCreateDto film, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "films/new";
        }
        try {
            filmService.createFilm(film);
            return "redirect:/api/films";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "films/new";
        }
    }

    @GetMapping(path = "/update/{id}")
    public String updateFilm(@PathVariable Long id, Model model) {
        model.addAttribute("film", filmService.getFilmById(id));
        model.addAttribute("filmId", id);
        return "films/update";
    }

    @PostMapping(path = "/update/{id}")
    public String updateFilm(@PathVariable Long id,
                             @Valid @ModelAttribute("film") FilmUpdateDto film,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "films/update";
        }
        try {
            filmService.updateFilm(id, film);
            return "redirect:/api/films/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "films/update";
        }
    }

    @PostMapping(path = "/delete")
    public String deleteFilm(@RequestParam Long id) {
        filmService.deleteFilmById(id);
        return "redirect:/api/films";
    }

    @GetMapping(path = "/{filmId}/review")
    public String review(@PathVariable Long filmId, Model model) {
        ReviewCreateDto createDto = new ReviewCreateDto();
        createDto.setFilmId(filmId);
        model.addAttribute("review", createDto);
        model.addAttribute("filmId", filmId);
        return "films/review";
    }

    @PostMapping(path = "/{filmId}/review")
    public String review(@PathVariable Long filmId, @Valid @ModelAttribute("review") ReviewCreateDto review, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "films/review";
        }
        try {
            filmService.addReview(review);
            return "redirect:/api/films/" + filmId;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "films/review";
        }
    }
}
