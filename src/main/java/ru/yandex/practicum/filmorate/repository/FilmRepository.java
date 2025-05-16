package ru.yandex.practicum.filmorate.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.entity.Film;
import ru.yandex.practicum.filmorate.model.enums.Genre;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    Optional<Film> findByName(String name);

    Optional<Film> findById(Long id);

    List<Film> findByGenre(Genre genre);

    @Query("SELECT f FROM Film f WHERE f.rating >= :minRating")
    List<Film> findFilmsWithRatingGreaterThanOrEqual(@Param("minRating") BigDecimal minRating);

}
