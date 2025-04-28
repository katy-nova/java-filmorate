package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDtoHtml;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.mapping.FilmMapping;
import ru.yandex.practicum.filmorate.dto.mapping.ReviewMapping;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class FilmServiceDB implements FilmService {

    private final FilmRepository filmRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);
    private final FilmMapping filmMapping;
    private final ReviewMapping reviewMapping;

    public List<FilmDto> getAllFilms() {
        return filmRepository.findAll().stream().map(filmMapping::toDto).collect(Collectors.toList());
    }

    public List<FilmDtoHtml> getAllFilmsHtml() {
        return filmRepository.findAll().stream().map(filmMapping::toDtoHtml).collect(Collectors.toList());
    }

    public FilmDto getFilmById(Long id) {
        return filmMapping.toDto(getFilm(id));
    }

    private Film getFilm(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Фильм с таким id не найден";
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    public FilmDto createFilm(FilmCreateDto film) {
        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            String errorMessage = "Дата релиза не может быть раньше " + EARLIEST_DATE;
            log.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        Film fromDTO = filmMapping.fromCreateDto(film);
        filmRepository.save(fromDTO);
        log.info("Фильм сохранен");
        return filmMapping.toDto(fromDTO);
    }

    public FilmDto updateFilm(Long id, FilmUpdateDto film) {
        log.debug("Попытка обновить данные фильма с ID: {}", id);
        Film oldFilm = getFilm(id);
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
        if ( film.getDuration() != null && film.getDuration() != 0 && !(film.getDuration().equals(oldFilm.getDuration()))) {
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
        filmRepository.save(oldFilm);
        log.info("фильм успешно сохранен");
        return filmMapping.toDto(oldFilm);
    }


    public void deleteFilmById(Long id) {
        Film film = getFilm(id); // проверяем, что фильм есть в базе
        log.debug("Попытка удаления всех отзывов к фильму с ID: {}", id);
        reviewRepository.deleteAllByFilmId(id);
        log.info("Отзывы успешно удалены");
        filmRepository.deleteById(id);
        log.info("Фильм с ID: {} успешно удален", id);
    }

    public List<FilmDto> getFilmsByGenre(Genre genre) {
        return filmRepository.findByGenre(genre).stream().map(filmMapping::toDto).collect(Collectors.toList());
    }

    public List<FilmDto> getFilmsByRating(BigDecimal rating) {
        return filmRepository.findFilmsWithRatingGreaterThanOrEqual(rating).stream()
                .map(filmMapping::toDto)
                .collect(Collectors.toList());
    }

    private void calculateRating(Review review) {
        Long id = review.getFilm().getId();
        BigDecimal rating = review.getRating();
        log.debug("Добавление оценки фильма с ID: {}", id);
        Film film = getFilm(id);
        int numberOfReviews = reviewRepository.findAllByFilmId(id).size();
        BigDecimal currentRating = film.getRating();
        if (currentRating == null) {
            log.debug("У фильма нет других оценок");
            film.setRating(rating);
            log.info("Новый рейтинг фильма: {}, количество оценок: {}", rating, 1);
            filmRepository.save(film);
            return;
        }
        BigDecimal numberOfReviewsBigDecimal = BigDecimal.valueOf(numberOfReviews);
        log.debug("Расчет нового рейтинга");
        BigDecimal newRating = currentRating.multiply(numberOfReviewsBigDecimal.subtract(BigDecimal.ONE))
                .add(rating)
                .divide(numberOfReviewsBigDecimal, 1, RoundingMode.HALF_UP);

        film.setRating(newRating);
        log.info("Новый рейтинг фильма: {}, количество оценок: {}", newRating, numberOfReviews);
        filmRepository.save(film);
    }

    public ReviewDto addReview(ReviewCreateDto review) {
        log.debug("Попытка добавления отзыва к фильму: {}", review.getFilmId());
        Optional<Review> oldReview = reviewRepository.findByUserIdAndFilmId(review.getUserId(), review.getFilmId());
        if (oldReview.isPresent()) {
            String errorMessage = String.format("Пользователь с Id: %d уже оставил отзыв от фильме с Id: %d",
                    review.getUserId(), review.getFilmId());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        Review fromDto = reviewMapping.fromDto(review);
        reviewRepository.save(fromDto);
        log.info("Отзыв к фильму с id: {} успешно сохранен", review.getFilmId());
        calculateRating(fromDto);
        return reviewMapping.toReviewDto(fromDto);
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
            String username = userRepository.findById(review.getUser().getId()).get().getLogin();
            reviewsWithLogins.put(username, review);
        }
        return reviewsWithLogins;
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {String errorMessage = String.format("Пользователь с id '%s' не найден", id);
                    log.error(errorMessage);
                    return new NotFoundException(errorMessage);
                });
    }

    @Override
    public FilmDto addLike(Long filmId, Long userId) {
        User user = findById(userId);
        Film film = getFilm(filmId);
        user.likeFilm(film);
        userRepository.save(user);
        filmRepository.save(film);
        return filmMapping.toDto(film);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        User user = findById(userId);
        Film film = getFilm(filmId);
        user.unlikeFilm(film);
        userRepository.save(user);
        filmRepository.save(film);
    }

    @Override
    public List<FilmDto> getTheMostLikedFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt((FilmDto filmDto) -> filmDto.getLikedBy().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
