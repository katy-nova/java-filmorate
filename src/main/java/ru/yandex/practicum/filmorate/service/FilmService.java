package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class FilmService {

    private final FilmRepository filmRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    public List<Film> getAllFilms() {
        return filmRepository.findAll();
    }

    public Film getFilmById(Long id) {
        return filmRepository.findById(id).orElseThrow(() -> {
            String errorMessage = "Фильм с таким id не найден";
            log.error(errorMessage);
            return new IllegalArgumentException(errorMessage);
        });
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            String errorMessage = "Дата релиза не может быть раньше " + EARLIEST_DATE;
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        log.info("Фильм сохранен");
        return filmRepository.save(film);
    }

    public Film updateFilm(Film film) {
        log.debug("Попытка обновить данные фильма с ID: {}", film.getId());
        Film oldFilm = getFilmById(film.getId());
        if (film.getName() != null && !film.getName().equals(oldFilm.getName())) {
            oldFilm.setName(film.getName());
            log.info("Название фильма: {} успешно обновлено", oldFilm.getName());
        }
        if (film.getDescription() != null && !film.getDescription().equals(oldFilm.getDescription())) {
            oldFilm.setDescription(film.getDescription());
            log.info("Описание фильма: {} успешно обновлено", oldFilm.getDescription());
        }
        if (film.getGenre() != null && !film.getGenre().equals(oldFilm.getGenre())) {
            oldFilm.setGenre(film.getGenre());
            log.info("Жанр фильма: {} успешно обновлен", oldFilm.getGenre());
        }
        if (film.getDuration() != 0 && !(film.getDuration() == oldFilm.getDuration())) {
            oldFilm.setDuration(film.getDuration());
            log.info("Продолжительность фильма: {} успешно обновлена", oldFilm.getDuration());
        }
        if (film.getReleaseDate() != null && !film.getReleaseDate().equals(oldFilm.getReleaseDate())) {
            if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
                String errorMessage = "Дата релиза не может быть раньше " + EARLIEST_DATE;
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.info("Дата релиза: {} успешно обновлена", oldFilm.getReleaseDate());
        }
        // не хочется, чтобы количество оценок и рейтинг можно было менять вручную
        return filmRepository.save(oldFilm);
    }

    public Film updateFilm(Long id, String name, String description, Genre genre, int duration,
                           LocalDate releaseDate) {
        Film film = getFilmById(id);
        if (film.getName() != null && !film.getName().equals(name)) {
            film.setName(name);
        }
        if (description != null && !description.equals(film.getDescription())) {
            if (description.length() > 200) {
                throw new ValidationException("Длинна описания не должна превышать 200 символов");
            }
            film.setDescription(description);
        }
        if (genre != null && !genre.equals(film.getGenre())) {
            film.setGenre(genre);
        }
        if (duration != 0 && duration != film.getDuration()) {
            film.setDuration(duration);
        }
        if (releaseDate != null && !releaseDate.equals(film.getReleaseDate())) {
            if (releaseDate.isBefore(EARLIEST_DATE)) {
                throw new ValidationException("Дата релиза не может быть раньше " + EARLIEST_DATE);
            }
            film.setReleaseDate(releaseDate);
        }
        return filmRepository.save(film);
    }

    public void deleteFilmById(Long id) {
        Film film = getFilmById(id); // проверяем, что фильм есть в базе
        log.debug("Попытка удаления всех отзывов к фильму с ID: {}", id);
        reviewRepository.deleteAllByFilmId(id);
        log.info("Отзывы успешно удалены");
        filmRepository.deleteById(id);
        log.info("Фильм с ID: {} успешно удален", id);
    }

    public List<Film> getFilmsByGenre(Genre genre) {
        return filmRepository.findByGenre(genre);
    }

    public List<Film> getFilmsByRating(BigDecimal rating) {
        return filmRepository.findFilmsWithRatingGreaterThanOrEqual(rating);
    }

    private Film calculateRating(Review review) {
        // стоит ли перенести этот метод в класс фильм и закрыть сеттеры к полям Рейтинг и Количество оценок, чтобы их
        // нельзя было установить вручную или вся бизнес-логика должна быть в классе сервиса?
        Long id = review.getFilmId();
        BigDecimal rating = review.getRating();
        log.debug("Добавление оценки фильма с ID: {}", id);
        Film film = getFilmById(id);
        int numberOfReviews = reviewRepository.findAllByFilmId(id).size();
        BigDecimal currentRating = film.getRating();
        if (currentRating == null) {
            log.debug("У фильма нет других оценок");
            film.setRating(rating);
            log.info("Новый рейтинг фильма: {}, количество оценок: {}", rating, 1);
            return filmRepository.save(film);
        }
        BigDecimal numberOfReviewsBigDecimal = BigDecimal.valueOf(numberOfReviews);
        log.debug("Расчет нового рейтинга");
        BigDecimal newRating = currentRating.multiply(numberOfReviewsBigDecimal)
                .add(rating)
                .divide(numberOfReviewsBigDecimal, 1, RoundingMode.HALF_UP);

        film.setRating(newRating);
        log.info("Новый рейтинг фильма: {}, количество оценок: {}", newRating, numberOfReviews);
        return filmRepository.save(film);
    }

    public Review addReview(Review review) {
        log.debug("Попытка добавления отзыва к фильму: {}", review.getFilmId());
        Optional<Review> oldReview = reviewRepository.findByUserIdAndFilmId(review.getUserId(), review.getFilmId());
        if (oldReview.isPresent()) {
            String errorMessage = String.format("Пользователь с Id: %d уже оставил отзыв от фильме с Id: %d",
                    review.getUserId(), review.getFilmId());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        reviewRepository.save(review);
        log.info("Отзыв к фильму с id: {} успешно сохранен", review.getFilmId());
        calculateRating(review);
        return review;
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public void deleteReviewsByUserId(Long userId) {
        reviewRepository.deleteAllByUserId(userId);
    }

    public void deleteReviewsByFilmId(Long filmId) {
        reviewRepository.deleteAllByFilmId(filmId);
    }

    public List<Review> getReviewsByFilmId(Long filmId) {
        return reviewRepository.findAllByFilmId(filmId);
    }

    public HashMap<String, Review> getReviewsWithLogins(Long filmId) {
        List<Review> reviews = reviewRepository.findAllByFilmId(filmId);
        if (reviews.isEmpty()) {
            return new HashMap<>();
        }
        HashMap<String, Review> reviewsWithLogins = new HashMap<>();
        for (Review review : reviews) {
            String username = userRepository.findById(review.getUserId()).get().getLogin();
            reviewsWithLogins.put(username, review);
        }
        return reviewsWithLogins;
    }
}
