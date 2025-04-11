package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(path = "/films")
@AllArgsConstructor
public class FilmWebController {

    private final FilmService filmService;

    @GetMapping
    public String getFilms(Model model) {
        List<Film> films = filmService.getAllFilms();
        model.addAttribute("films", films);
        return "films/list";
    }

    @GetMapping(path = "/{id}")
    public String getFilm(@PathVariable Long id, Model model) {
        Film film = filmService.getFilmById(id);
        HashMap<String, Review> reviews = filmService.getReviewsWithLogins(id);
        model.addAttribute("reviews", reviews);
        model.addAttribute("numberOfReviews", reviews.size());
        model.addAttribute("film", film);
        return "films/filmInfo";
    }

    @GetMapping(path = "/new")
    public String newFilm(Model model) {
        model.addAttribute("film", new Film());
        return "films/new";
    }

    @PostMapping(path = "/new")
    public String saveFilm(@Valid @ModelAttribute("film") Film film, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "films/new";
        }
        try {
            filmService.createFilm(film);
            return "redirect:/films/" + film.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "films/new";
        }
    }

    @GetMapping(path = "/update/{id}")
    public String updateFilm(@PathVariable Long id, Model model) {
        model.addAttribute("film", filmService.getFilmById(id));
        return "films/update";
    }

    @PostMapping(path = "/update/{id}")
    public String updateFilm(@PathVariable Long id,
                             @Valid @ModelAttribute("film") Film film,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "films/update";
        }
        try {
            filmService.updateFilm(film);
            return "redirect:/films/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "films/update";
        }
    }

    @PostMapping(path = "/delete")
    public String deleteFilm(@RequestParam Long id) {
        filmService.deleteFilmById(id);
        return "redirect:/films";
    }

    @GetMapping(path = "/{filmId}/review")
    public String review(@PathVariable Long filmId, Model model) {
        model.addAttribute("review", new Review());
        model.addAttribute("filmId", filmId);
        return "films/review";
    }

    @PostMapping(path = "/{filmId}/review")
    public String review(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "films/review";
        }
        try {
            filmService.addReview(review);
            return "redirect:/films/" + review.getFilmId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "films/review";
        }
    }
}
